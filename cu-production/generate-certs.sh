#!/bin/bash
########################################################
CA_CERT=ca.pem
CA_KEY=ca-key.pem
CLIENT_CERT=cert.pem
CLIENT_KEY=key.pem
SERVER_CERT=server.pem
SERVER_KEY=server-key.pem
PASSPHRASE=changeit

# Origin from https://raw.githubusercontent.com/ksahnine/docker/master/gen-docker-certs.sh

#if [[ $USER != "root" ]]; then
#    echo "This script must be run as admincu"
#    exit 1
#fi

dhostname="localhost"
dip="172.17.42.1"

## Clean
sudo rm -f *.pem

## Generation du certificat CA
sudo openssl genrsa -aes256 -passout pass:$PASSPHRASE -out $CA_KEY 2048
sudo openssl req -new -x509 -days 365 -key $CA_KEY -sha256 -passin pass:$PASSPHRASE -subj "/C=FR/ST=MyState/O=Treeptik" -out $CA_CERT

## Generation du certificat Serveur
sudo openssl genrsa -out $SERVER_KEY 2048
sudo openssl req -subj "/CN=${dhostname}" -new -key $SERVER_KEY -out server.csr 2>/dev/null
echo subjectAltName = IP:${dip} > extfile.cnf
sudo openssl x509 -passin pass:$PASSPHRASE -req -days 365 -in server.csr -CA $CA_CERT -CAkey $CA_KEY -CAcreateserial -out $SERVER_CERT -extfile extfile.cnf

## Generation du certificat Client
sudo openssl genrsa -out $CLIENT_KEY 2048
sudo openssl req -subj '/CN=client' -new -key $CLIENT_KEY -out client.csr 2>/dev/null
echo extendedKeyUsage = clientAuth > extfile.cnf
sudo openssl x509 -passin pass:$PASSPHRASE -req -days 365 -in client.csr -CA $CA_CERT -CAkey $CA_KEY -CAcreateserial -out $CLIENT_CERT -extfile extfile.cnf

## Nettoyage
sudo rm -f client.csr server.csr extfile.cnf ca.srl
sudo chmod 0400 $CA_KEY $CLIENT_KEY $SERVER_KEY
sudo chmod 0444 $CA_CERT $SERVER_CERT $CLIENT_CERT

## Instruction
echo ""
echo " - Server side : copying into [/root/.docker] les fichiers [$CA_CERT $SERVER_CERT $SERVER_KEY]"
sudo mkdir /root/.docker
sudo cp -f $CA_CERT /root/.docker
sudo mv -f $SERVER_CERT /root/.docker
sudo mv -f $SERVER_KEY /root/.docker

echo " - Client side : copying into [/home/admincu/.docker] les fichiers [$CA_CERT $CLIENT_CERT $CLIENT_KEY]"
sudo mkdir /home/admincu/.docker
sudo mv -f $CA_CERT /home/admincu/.docker
sudo mv -f $CLIENT_CERT /home/admincu/.docker
sudo mv -f $CLIENT_KEY /home/admincu/.docker
sudo chown -R admincu /home/admincu/.docker

# That's all folks
