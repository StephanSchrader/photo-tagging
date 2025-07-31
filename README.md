# Photo Tagging

AI photo tagging, inspired by: https://www.heise.de/ratgeber/Wie-eine-lokale-KI-die-Fotosammlung-auf-dem-NAS-verschlagworten-kann-9685509.html

## Run

    docker run -d -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama

Load model:

    docker exec -it ollama ollama pull llava:v1.6

    
## Links

https://ollama.com/library/llava

https://ollama.com/blog/ollama-is-now-available-as-an-official-docker-image