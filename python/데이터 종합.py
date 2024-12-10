import pandas as pd
from datetime import datetime, timedelta
import re

# 현재 시간
current_time = datetime.now()
current_date_str = current_time.strftime("%Y-%m-%d")  # 현재 날짜를 YYYYMMDD 형식으로 변환

# 데이터 파일 경로
input_file = f"./data/슈카월드_정보_{current_date_str}.xlsx"  # 기존 데이터 파일 이름
output_file = f"./data/슈카월드_최종_정보_{current_date_str}.xlsx"  # 최종 데이터 저장 파일

# 데이터 로드
try:
    df = pd.read_excel(input_file)
except FileNotFoundError:
    print(f"파일을 찾을 수 없습니다: {input_file}")
    exit()

# 함수: "업로드 일자" 계산
def calculate_upload_time(uploaded):
    try:
        if "년 전" in uploaded:
            years = int(uploaded.split("년 전")[0])
            delta = timedelta(days=years * 365)  # 1년 = 365일로 계산
        elif "개월 전" in uploaded:
            months = int(uploaded.split("개월 전")[0])
            delta = timedelta(days=months * 30)  # 1개월 = 30일로 계산
        elif "주 전" in uploaded:
            weeks = int(uploaded.split("주 전")[0])
            delta = timedelta(weeks=weeks)
        elif "일 전" in uploaded:
            days = int(uploaded.split("일 전")[0])
            delta = timedelta(days=days)
        elif "시간 전" in uploaded:
            hours = int(uploaded.split("시간 전")[0])
            delta = timedelta(hours=hours)
        elif "분 전" in uploaded:
            minutes = int(uploaded.split("분 전")[0])
            delta = timedelta(minutes=minutes)
        else:
            return None  # 예상 외 형식일 경우 None 반환

        # 최종 업로드 시간 계산
        final_upload_time = current_time - delta
        return final_upload_time.strftime("%Y-%m-%d %H:%M")
    except Exception as e:
        print(f"오류 발생: {e}")
        return None

# '업로드 일자' 컬럼 추가
df['업로드 일자'] = df['uploaded'].apply(calculate_upload_time)

# 'uploaded' 컬럼 제거
df.drop(columns=['uploaded'], inplace=True)

# 함수: 숫자만 추출
def extract_numeric_views(views):
    try:
        numeric_part = re.sub(r'[^\d]', '', views)  # 숫자가 아닌 모든 문자 제거
        return int(numeric_part)
    except:
        return 0

# 'views' 컬럼 숫자로 변환
df['views_numeric'] = df['views'].apply(extract_numeric_views)

# 평균 조회수 계산 함수
def calculate_average_views(row):
    try:
        # 업로드 일자가 None이면 None 반환
        if pd.isna(row['업로드 일자']):
            return None

        view_count = row['views_numeric']
        upload_date = datetime.strptime(row['업로드 일자'], "%Y-%m-%d %H:%M")
        days_since_upload = (current_time - upload_date).days

        if days_since_upload <= 14:  # 14일 이내
            average_views = view_count / max(days_since_upload, 1)  # 0일 방지
        else:  # 14일 이후
            average_views = view_count / 14

        return round(average_views, 2)  # 평균 조회수 소수점 2자리로
    except Exception as e:
        print(f"평균 조회수 계산 오류: {e}")
        return None

# '평균 조회수' 컬럼 추가
df['평균 조회수'] = df.apply(calculate_average_views, axis=1)

# 전체 평균 조회수 계산 및 추가
def add_overall_average(df):
    valid_average_views = df['평균 조회수'].dropna()
    if len(valid_average_views) > 0:
        overall_average = round(valid_average_views.mean(), 2)  # 전체 평균 계산
    else:
        overall_average = None  # 값이 없으면 None

    # '전체 평균 조회수' 컬럼 추가
    df['전체 평균 조회수'] = overall_average  # 모든 행에 동일한 값 추가

    return df

# 전체 평균 조회수 추가
df = add_overall_average(df)

# 업로드 일자가 1년 이내인 항목 필터링
one_year_ago = current_time - timedelta(days=365)
recent_df = df[pd.to_datetime(df['업로드 일자'], errors='coerce') >= one_year_ago]

# 업로드 일자를 기준으로 가장 최근 순서로 정렬
sorted_recent_df = recent_df.sort_values(by='업로드 일자', ascending=False)

# 최종 데이터 저장
sorted_recent_df.to_excel(output_file, index=False)

# 출력 메시지
print(f"업로드 일자가 1년 이내인 항목 중 가장 최근 순으로 정렬된 데이터가 저장되었습니다: {output_file}")
