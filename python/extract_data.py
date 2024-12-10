import pandas as pd
import json

# 엑셀 파일 경로
file_path = f"./data/슈카월드_최종_정보_{pd.Timestamp.now().strftime('%Y-%m-%d')}.xlsx"

# 엑셀 읽기
df = pd.read_excel(file_path)

# 필요한 컬럼 선택 (첫 번째 행만 가져오기)
selected_row = df[['title', 'link', 'views', '업로드 일자', '평균 조회수', '전체 평균 조회수']].iloc[0]

# JSON 파일로 저장
output_json = [selected_row.to_dict()]  # 단일 행도 리스트로 감싸서 JSON 형식 유지
with open('./data/post_data.json', 'w', encoding='utf-8') as json_file:
    json.dump(output_json, json_file, ensure_ascii=False, indent=4)
