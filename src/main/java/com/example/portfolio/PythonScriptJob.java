package com.example.portfolio;

import com.example.portfolio.domain.Post;
import com.example.portfolio.domain.Post2;
import com.example.portfolio.repository.PostRepository;
import com.example.portfolio.repository.Post2Repository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component
public class PythonScriptJob implements Job {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private Post2Repository post2Repository;

    @Override
    public void execute(JobExecutionContext context) {
        try {
            // Python 스크립트 경로
            String baseScriptPath = "C:\\Users\\user\\Desktop\\portfolio\\python\\";
            String[] scripts = {"슈카.py", "데이터 종합.py","연합.py", "extract_data.py", "extract_data2.py"};

            // 스크립트를 순차적으로 실행
            for (String script : scripts) {
                if (runPythonScript(baseScriptPath + script)) {
                    System.out.println(script + " 실행 성공.");
                } else {
                    System.err.println(script + " 실행 실패.");
                    return; // 하나라도 실패 시 중단
                }
            }

            // 첫 번째 JSON 파일 처리 (슈카월드 데이터)
            processPostData();

            // 두 번째 JSON 파일 처리 (연합뉴스 데이터)
            processPostData2();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processPostData() throws Exception {
        // JSON 파일 읽기 (슈카월드 데이터)
        String jsonFilePath = "./data/post_data.json";
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> postData = mapper.readValue(
                new File(jsonFilePath),
                new TypeReference<List<Map<String, String>>>() {}
        );

        // 데이터베이스에 저장
        for (Map<String, String> data : postData) {
            // YouTube 링크 변환
            String originalUrl = data.get("link");
            String embedUrl = convertToEmbedUrl(originalUrl);

            Post post = Post.builder()
                    .title(data.get("title"))
                    .views(data.get("views"))
                    .uploadDay(data.get("업로드 일자"))
                    .anviews(data.get("평균 조회수"))
                    .allanv(data.get("전체 평균 조회수"))
                    .URLLink(embedUrl) // 변환된 URL 저장
                    .build();
            postRepository.save(post);
        }
    }

    private void processPostData2() throws Exception {
        // JSON 파일 읽기 (연합뉴스 데이터)
        String jsonFilePath = "./data/post_data2.json";
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> postData = mapper.readValue(
                new File(jsonFilePath),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        // 데이터베이스에 저장
        for (Map<String, Object> data : postData) {
            Post2 post2 = Post2.builder()
                    .popularWord((String) data.get("인기 단어"))
                    .count(Integer.parseInt(data.get("횟수").toString()))
                    .relatedWords((String) data.get("연관 단어"))
                    .link1((String) data.get("링크 1"))
                    .link2((String) data.get("링크 2"))
                    .build();
            post2Repository.save(post2);
        }
    }

    /**
     * YouTube URL을 embed 형식으로 변환
     */
    public String convertToEmbedUrl(String url) {
        if (url != null && url.contains("watch?v=")) {
            return url.replace("watch?v=", "embed/");
        }
        return url; // 이미 embed 형식이거나 null이라면 그대로 반환
    }

    /**
     * Python 스크립트를 실행하는 메서드
     */
    private boolean runPythonScript(String scriptPath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath);
            processBuilder.inheritIO(); // 콘솔 출력 보기
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
