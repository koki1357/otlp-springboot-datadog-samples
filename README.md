
### 01. システム構成図
![alt text](image.png)

### 02. Datadogへのデータ連携方法

#### 02-01. datadog-agentとotel-collectorを起動

- `docker-compose up -d `

#### 02-02.ローカルでアプリケーションを実行

- `./gradlew bootRun`