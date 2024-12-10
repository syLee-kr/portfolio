import os
import requests
from bs4 import BeautifulSoup
from datetime import datetime
from collections import Counter, defaultdict
from konlpy.tag import Okt
import pandas as pd

def 기사_크롤링():
    # 기본 설정
    BASE_URL = "https://www.yna.co.kr/news/"
    articles = []

    # 1부터 20페이지까지 크롤링
    for page_num in range(1, 21):
        url = f"{BASE_URL}{page_num}"
        response = requests.get(url)
        if response.status_code != 200:
            print(f"{page_num}페이지 접속 실패. 상태 코드: {response.status_code}")
            continue

        soup = BeautifulSoup(response.text, 'html.parser')

        # 기사 제목과 링크 추출
        title_elements = soup.find_all("a", class_="tit-wrap")
        for element in title_elements:
            title_tag = element.find("strong", class_="tit-news")
            if title_tag:
                title = title_tag.get_text(strip=True)
                link = element['href']

                # 절대 경로 처리
                if not link.startswith("http"):
                    link = f"https://www.yna.co.kr{link}"

                articles.append({'title': title, 'link': link})

        print(f"{page_num}페이지 크롤링 완료. 총 {len(articles)}개의 기사 수집.")

    return articles

def 제목_분석(articles):
    # 형태소 분석기 초기화
    okt = Okt()

    # 제목 리스트 추출
    titles = [article['title'] for article in articles]

    # 명사 추출
    title_words = [okt.nouns(title) for title in titles]

    # 한 글자 단어 제거
    title_words = [[word for word in words if len(word) > 1] for words in title_words]

    # 단어 빈도 계산
    all_words = [word for words in title_words for word in words]
    word_counts = Counter(all_words)

    # 단어 연관성 계산
    word_relationships = defaultdict(list)
    for words in title_words:
        for i, word in enumerate(words):
            related_words = [w for j, w in enumerate(words) if j != i]
            word_relationships[word].extend(related_words)

    # 상위 10개 인기 단어 추출
    top_words = word_counts.most_common(10)

    # 각 인기 단어에 대한 데이터 준비
    data = []
    for word, count in top_words:
        # 가장 많이 함께 등장한 두 개의 연관 단어 추출
        related = Counter(word_relationships[word]).most_common(2)
        related_words = [w for w, _ in related]

        # 인기 단어와 연관 단어를 포함하는 기사 링크 수집
        links = []
        for article in articles:
            if word in article['title']:
                if all(rw in article['title'] for rw in related_words):
                    links.append(article['link'])
                elif any(rw in article['title'] for rw in related_words):
                    links.append(article['link'])
                else:
                    links.append(article['link'])

        # 중복 링크 제거
        links = list(dict.fromkeys(links))

        # 데이터 구성
        row = {
            '인기 단어': word,
            '횟수': count,
            '연관 단어': ', '.join(related_words)
        }

        # 링크 추가
        for idx, link in enumerate(links):
            row[f'링크 {idx + 1}'] = link

        data.append(row)

    return data

def 엑셀로_저장(data):
    # 데이터프레임 생성
    df = pd.DataFrame(data)

    # 저장 경로 설정
    today_str = pd.Timestamp.now().strftime('%Y-%m-%d')
    output_file = f"./data/연합뉴스_{today_str}.xlsx"

    # 엑셀로 저장
    df.to_excel(output_file, index=False)
    print(f"결과가 저장되었습니다: {output_file}")

def main():
    # 1단계: 기사 크롤링
    articles = 기사_크롤링()

    # 2단계: 제목 분석
    data = 제목_분석(articles)

    # 3단계: 엑셀로 저장
    엑셀로_저장(data)

if __name__ == "__main__":
    main()
