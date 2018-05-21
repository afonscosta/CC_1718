cp -r ServerReverseProxy ~/Desktop

cd ~/Desktop/ServerReverseProxy/src/serverreverseproxy/

javac -d . ServerReverseProxy.java MonitorUDP.java EntradaTabelaEstado.java ServerWorker.java

javac -d . AgenteUDP.java PDU_AM.java PDU_MA.java HMAC.java Converter.java

javac -d . MonitorUDP.java PDU_AM.java PDU_MA.java HMAC.java Converter.java EntradaTabelaEstado.java

javac -d . PDU_AM.java PDU_MA.java HMAC.java

javac -d . PDU_MA.java PDU_AM.java HMAC.java

javac -d . HMAC.java
