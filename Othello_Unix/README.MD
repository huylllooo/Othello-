C7 - NETWORK PROGRAMMING - REVERSI
=========

ビルド&起動方法
---------------

ビルドをするには，以下をタイプする．

    ant jar

すると`c7.jar`ができる．

    java -jar c7.jar server PORT

でPORTで接続を待ち受けるエコーサーバを起動する．

    java -jar c7.jar client HOST PORT

又は

    java -jar c7.jar rclient HOST PORT

もしくは

    java -jar c7.jar hclient HOST PORT

でHOSTのPORTに接続するエコークライアントを起動する.

