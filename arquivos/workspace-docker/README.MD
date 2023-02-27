## WildFly 13 EGY	

Esse é uma imagem docker do WildFly customizada para a EGY

## Comando para gerar a imagem do wildfly customizada
docker build -t gnfe/wildfly:1.0 .

## Comando para rodar o docker compose
docker-compose up -d

## Comando para parar o docker compose
docker-compose down

## Comando para listar os containers rodando
docker ps
docker ps -a

## Comando para abrir o terminal
docker exec -it gnfe /bin/bash

## Comando exibir os los do container
`docker logs [id da imagem] -f`

## Comando para parar o container
`docker stop [id da imagem]`

## Habilitar serviço do docker
`sudo systemctl enable docker.service` (habilitar serviço após instalar)

## Iniciar serviço
`sudo systemctl start docker.service`
