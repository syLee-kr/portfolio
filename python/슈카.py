from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import pandas as pd
import time
from datetime import datetime
import os

# 데이터 저장 디렉토리 설정
DATA_DIR = "./data"
if not os.path.exists(DATA_DIR):
    os.makedirs(DATA_DIR)

# 1. Selenium 브라우저 설정
def setup_browser():
    options = webdriver.ChromeOptions()
    options.add_argument("--headless")  # 브라우저를 띄우지 않고 실행
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("start-maximized")
    options.add_argument("disable-infobars")
    options.add_argument("--disable-extensions")

    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
    return driver

# 2. 유튜브 채널 크롤링
def scrape_youtube_videos(url):
    driver = setup_browser()
    driver.get(url)

    # 페이지가 로드되도록 대기
    time.sleep(5)

    # 스크롤을 끝까지 내리기 (더 많은 데이터를 로드하기 위해)
    last_height = driver.execute_script("return document.documentElement.scrollHeight")
    while True:
        driver.execute_script("window.scrollTo(0, document.documentElement.scrollHeight);")
        time.sleep(3)  # 스크롤 후 로딩 대기
        new_height = driver.execute_script("return document.documentElement.scrollHeight")
        if new_height == last_height:  # 더 이상 스크롤할 내용이 없을 경우
            break
        last_height = new_height

    # 동영상 정보 크롤링
    videos = driver.find_elements(By.XPATH, '//a[@id="video-title-link"]')
    video_data = []
    for video in videos:
        try:
            title = video.get_attribute("aria-label").split(" 게시자: ")[0]  # 제목
            link = video.get_attribute("href")  # 링크
            aria_label = video.get_attribute("aria-label")

            # 조회수와 업로드 날짜 추출
            if "조회수" in aria_label and "전" in aria_label:
                parts = aria_label.split(" 조회수 ")
                views = parts[1].split(" ")[0]  # 조회수
                uploaded = " ".join(parts[1].split(" ")[1:])  # 업로드 일자
            else:
                views, uploaded = "N/A", "N/A"

            video_data.append({
                "title": title,
                "link": link,
                "views": views,
                "uploaded": uploaded
            })
        except Exception as e:
            print(f"오류 발생: {e}")
            continue

    driver.quit()
    return video_data

# 3. 데이터 저장
def save_to_excel(data):
    # 현재 날짜를 기준으로 파일명 생성
    current_date_str = datetime.now().strftime("%Y-%m-%d")
    filename = os.path.join(DATA_DIR, f"슈카월드_정보_{current_date_str}.xlsx")

    df = pd.DataFrame(data)
    df.to_excel(filename, index=False)  # encoding 인자 제거
    print(f"데이터가 {filename}에 저장되었습니다.")

# 실행
if __name__ == "__main__":
    URL = "https://www.youtube.com/@syukaworld/videos"
    video_data = scrape_youtube_videos(URL)
    if video_data:
        save_to_excel(video_data)
    else:
        print("영상 데이터를 가져올 수 없습니다.")
