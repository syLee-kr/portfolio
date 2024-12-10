import pandas as pd
import json
import os

def extract_data():
    # 오늘 날짜로 파일명 생성
    today_str = pd.Timestamp.now().strftime('%Y-%m-%d')
    file_path = f"./data/연합뉴스_{today_str}.xlsx"

    # 엑셀 파일이 존재하는지 확인
    if not os.path.exists(file_path):
        print(f"파일을 찾을 수 없습니다: {file_path}")
        return

    # 엑셀 파일 읽기
    df = pd.read_excel(file_path)

    # 필요한 컬럼 선택
    selected_columns = ['인기 단어', '횟수', '연관 단어', '링크 1', '링크 2']
    df_selected = df[selected_columns]

    # 데이터프레임을 딕셔너리 리스트로 변환
    data = df_selected.to_dict(orient='records')

    # JSON 파일로 저장
    output_json_path = './data/post_data2.json'
    with open(output_json_path, 'w', encoding='utf-8') as json_file:
        json.dump(data, json_file, ensure_ascii=False, indent=4)

    print(f"데이터가 JSON 파일로 저장되었습니다: {output_json_path}")

if __name__ == "__main__":
    extract_data()
