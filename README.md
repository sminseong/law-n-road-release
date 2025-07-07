# Law-n-road Ubuntu 배포 서버 세팅 가이드

---

## 🚀 Public 서버

### 📥 프로젝트 클론

```bash
sudo apt update
sudo apt upgrade -y

# Git & Nginx 설치
sudo apt install -y git nginx

cd /opt
git clone https://github.com/law-n-road/law-n-road-release.git
```

---

### 🐳 OpenVidu 설치 (Docker)

```bash
# Docker 설치 및 실행
sudo apt install -y docker.io
sudo systemctl start docker
sudo systemctl enable docker

# Docker Compose 설치
sudo apt install -y docker-compose

# 도커 권한 추가 (현재 사용자)
sudo usermod -aG docker $USER

# OpenVidu 최신버전 설치
cd /opt
curl https://s3-eu-west-1.amazonaws.com/aws.openvidu.io/install_openvidu_latest.sh | bash
cd openvidu
```

---

### 🔒 HTTPS 인증서 발급 (Let's Encrypt)

```bash
sudo apt-get install letsencrypt
sudo systemctl stop nginx

sudo letsencrypt certonly --standalone -d lawnroad.kr
# 이메일 작성, Yes 입력, 뉴스레터 No 선택

# 인증서 발급 확인
cd /etc/letsencrypt/live/lawnroad.kr
ls  # cert.pem, chain.pem, fullchain.pem, privkey.pem 확인
```

---

### ⚙️ OpenVidu 설정 (.env)

```bash
nano /opt/openvidu/.env
```

```
DOMAIN_OR_PUBLIC_IP=lawnroad.kr
OPENVIDU_SECRET=lawnroad1234
CERTIFICATE_TYPE=letsencrypt
LETSENCRYPT_EMAIL=등록한 이메일
HTTP_PORT=8442
HTTPS_PORT=8443

# 비트레이트 설정 (옵션)
OPENVIDU_STREAMS_VIDEO_MAX_RECV_BANDWIDTH=1500
OPENVIDU_STREAMS_VIDEO_MIN_RECV_BANDWIDTH=300
OPENVIDU_STREAMS_VIDEO_MAX_SEND_BANDWIDTH=1500
OPENVIDU_STREAMS_VIDEO_MIN_SEND_BANDWIDTH=300
```

---

### 📂 권한 설정

```bash
sudo chmod 755 /etc/letsencrypt/live/lawnroad.kr
sudo chmod 755 /etc/letsencrypt/archive/lawnroad.kr
sudo chmod 644 /etc/letsencrypt/live/lawnroad.kr/*
```

---

### 🛠️ Docker Compose 수정

```bash
nano /opt/openvidu/docker-compose.yml
```

```yaml
services:
  nginx:
    volumes:
      - /etc/letsencrypt:/etc/letsencrypt:ro
```

---

### 🔧 Nginx 설정파일 작성

```bash
sudo nano /etc/nginx/sites-available/lawroad-frontend
```

```nginx
server {
    listen 80;
    server_name lawnroad.kr;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name lawnroad.kr;

    root /opt/law-n-road-release/frontend/dist;
    index index.html;

    ssl_certificate /etc/letsencrypt/live/lawnroad.kr/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/lawnroad.kr/privkey.pem;

    client_max_body_size 10G;

    location /api/ {
        proxy_pass http://10.0.2.6:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /ws/ {
        proxy_pass http://10.0.2.6:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "Upgrade";
        proxy_set_header Host $host;
    }

    location /openvidu/ {
        proxy_pass https://localhost:443/;
        proxy_ssl_verify off;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "Upgrade";
        proxy_set_header Host $host;
    }

    location /oauth2/ {
        proxy_pass http://10.0.2.6:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header Cookie $http_cookie;
    }

    location /login/ {
        proxy_pass http://10.0.2.6:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header Cookie $http_cookie;
    }

    location /mail/ {
	    proxy_pass http://10.0.2.6:8080;
	    proxy_set_header Host $host;
	    proxy_set_header X-Real-IP $remote_addr;
	    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		}

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

```bash
sudo nginx -t && sudo systemctl restart nginx
docker-compose up -d
docker ps  # 컨테이너 확인
```

---

### 🚧 방화벽 규칙

| 프로토콜 | 포트 범위 | 출발지 IP | 설명                 |
|----------|-----------|------------|----------------------|
| TCP      | 22        | 내 IP 또는 0.0.0.0/0 | SSH (Termius 접속)   |
| TCP      | 80, 443   | 0.0.0.0/0 | HTTP/HTTPS           |
| TCP/UDP  | 3478      | 0.0.0.0/0 | TURN 서버(WebRTC)    |
| TCP/UDP  | 40000–65535 | 0.0.0.0/0 | WebRTC 미디어 포트  |

---

## 🖥️ Frontend 빌드

```bash
sudo apt install -y nodejs npm
sudo npm install -g @vue/cli

cd /opt/law-n-road/frontend
rm -rf node_modules package-lock.json
npm install
npm run build
```

### 📦 추가 NPM 설치

```bash
npm install axios solapi openvidu-browser@2.30.0 \
            sockjs-client @stomp/stompjs \
            @fullcalendar/vue3 @fullcalendar/daygrid @fullcalendar/interaction \
            @tiptap/vue-3 @tiptap/starter-kit \
            bootstrap html2pdf.js crypto-js webm-duration-fix chart.js
```

```bash
sudo rm -f /etc/nginx/sites-enabled/default
sudo ln -sf /etc/nginx/sites-available/lawroad-frontend /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
sudo systemctl enable nginx
```

---

## 🛠️ Private 서버 (Backend)

### ☕ Java 설치 및 빌드

```bash
sudo apt update
sudo apt install openjdk-17-jdk -y
java --version

cd /opt
git clone https://github.com/law-n-road/law-n-road-release.git

cd law-n-road-release
chmod +x ./gradlew
./gradlew build -x test
```

### 🐘 gradle 설치
```bash
apt install unzip wget -y

cd /opt
wget https://services.gradle.org/distributions/gradle-8.7-bin.zip
unzip gradle-8.7-bin.zip
ln -s /opt/gradle-8.7 /opt/gradle

echo 'export PATH=$PATH:/opt/gradle/bin' >> ~/.bashrc
source ~/.bashrc

gradle -v
```

### ⚙️ Backend 서비스 설정 (systemd)

```bash
sudo nano /etc/systemd/system/lawroad-backend.service
```

```ini
[Unit]
Description=LawNRoad Backend Service
After=network.target

[Service]
# 실행할 사용자: root 홈을 그대로 쓰신다면 User=root
# 일반 ubuntu 계정을 만드셨다면 User=ubuntu 로 바꿔주세요
User=root

# 워킹 디렉토리: 로그가 생성될 때 기준이 됩니다
WorkingDirectory=/opt/law-n-road-release

# 실제 실행 명령어: 
# build/libs 안에 생성된 JAR 파일 이름을 정확히 적어야 합니다.
ExecStart=/usr/bin/java -jar /opt/law-n-road-release/build/libs/law-n-road-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:mysql://10.0.2.7:3306/law_n_road \
  --spring.datasource.username=lawnroad \
  --spring.datasource.password=Pass1234!@ \
  --spring.profiles.active=prod \
  --spring.data.mongodb.host=10.0.2.7 \
  --spring.data.mongodb.port=27017 \
  --spring.data.mongodb.database=chatdb \
  --spring.data.mongodb.username=chat \
  --spring.data.mongodb.password=chat1234@ \
  --spring.data.mongodb.authentication-database=admin \
  --spring.data.redis.host=10.0.2.7 \
  --spring.data.redis.port=6379 \
  --spring.security.oauth2.client.registration.naver.client-name=naver \
  --spring.security.oauth2.client.registration.naver.client-id=Wy4hhh1etGeWpNOAGUTe \
  --spring.security.oauth2.client.registration.naver.client-secret=et1ATdUI0M \
  --spring.security.oauth2.client.registration.naver.redirect-uri=https://lawnroad.kr/login/oauth2/code/naver \
  --spring.security.oauth2.client.registration.naver.authorization-grant-type=authorization_code \
  --spring.security.oauth2.client.registration.naver.scope=name,email \
  --spring.security.oauth2.client.provider.naver.authorization-uri=https://nid.naver.com/oauth2.0/authorize \
  --spring.security.oauth2.client.provider.naver.token-uri=https://nid.naver.com/oauth2.0/token \
  --spring.security.oauth2.client.provider.naver.user-info-uri=https://openapi.naver.com/v1/nid/me \
  --spring.security.oauth2.client.provider.naver.user-name-attribute=response \
  --app.frontend.social-login-url=https://lawnroad.kr/login/oauth2/code/naver

Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable lawroad-backend
sudo systemctl restart lawroad-backend
```

---

## 🗃️ Private 서버 (Database)

### 🐬 MySQL 설치 및 설정

```bash
sudo apt update
sudo apt install mysql-server

sudo mysql -u root -p
```

```sql
CREATE DATABASE law_n_road;
CREATE USER 'lawnroad'@'10.0.2.6' IDENTIFIED BY 'Pass1234!@';
GRANT ALL PRIVILEGES ON law_n_road.* TO 'lawnroad'@'10.0.2.6';
FLUSH PRIVILEGES;
```

### ⚙️ 원격 접속 허용

```bash
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
# bind-address = 127.0.0.1 → 0.0.0.0
sudo systemctl restart mysql
```

---

이 가이드를 따라 완벽한 Law & Road 프로젝트 배포환경을 구축하세요! 🚀🎉
