cp -r ServerReverseProxy ~/Desktop

cd ServerReverseProxy/src/serverreverseproxy/

javac -d . AgenteUDP.java PDU_AM.java PDU_MA.java HMAC.java

javac -d . MonitorUDP.java PDU_AM.java PDU_MA.java HMAC.java

javac -d . PDU_AM.java PDU_MA.java HMAC.java

javac -d . PDU_MA.java PDU_AM.java HMAC.java

javac -d . HMAC.java

java serverreverseproxy.AgenteUDP

java serverreverseproxy.MonitorUDP
