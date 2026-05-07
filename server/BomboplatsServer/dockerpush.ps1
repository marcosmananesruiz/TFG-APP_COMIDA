# PARA EJECUTAR ESTO TIENES QUE TENER EL DOCKER INSTALADO Y LAS CREDENCIALES DE MARCOS DE DOCKER HUB

docker build -t bomboplats-server .
docker tag bomboplats-server marcosmananesruiz/bomboplats-server:latest
docker push marcosmananesruiz/bomboplats-server:latest