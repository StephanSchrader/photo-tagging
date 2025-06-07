# Photo Tagging

AI photo tagging, inspired by: https://www.heise.de/ratgeber/Wie-eine-lokale-KI-die-Fotosammlung-auf-dem-NAS-verschlagworten-kann-9685509.html

## Run

    docker run -d -v ollama:<path to ?>/.ollama -p 11434:11434 --name ollama ollama/ollama

Load model:

    docker exec -it ollama ollama pull llava:v1.6

    