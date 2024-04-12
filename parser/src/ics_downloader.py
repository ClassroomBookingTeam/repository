import requests
from bs4 import BeautifulSoup
import os
import shutil

header = "МГТУ им. Н. Э. Баумана"
url = "https://lks.bmstu.ru/schedule/list"
response = requests.get(url)

if response.status_code == 200:
    soup = BeautifulSoup(response.text, "html.parser")
    mgty_heading = soup.find("h1", text=header)

    if mgty_heading:
        next_sibling = mgty_heading.find_next_sibling()
        schedule_links = next_sibling.find_all(
            "a", href=lambda href: href and href.startswith("/schedule/")
        )

        save_path = "../docs/ics_files"
        os.makedirs(save_path, exist_ok=True)

        for link in schedule_links:
            schedule_url = "https://lks.bmstu.ru" + link["href"] + ".ics"
            file_name = link["href"].split("/")[-1] + ".ics"
            schedule_response = requests.get(schedule_url)

            if schedule_response.status_code == 200:
                with open(os.path.join(save_path, file_name), "wb") as f:
                    f.write(schedule_response.content)
                print(f"Файл '{file_name}' успешно загружен.")
            else:
                print(
                    f"Ошибка при загрузке файла '{file_name}': {schedule_response.status_code}"
                )
    else:
        print("Заголовок " + h1 + " не найден на странице.")
else:
    print("Ошибка при получении страницы:", response.status_code)
