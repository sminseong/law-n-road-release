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

### ⚙️ Backend 서비스 설정 (systemd)

```bash
sudo nano /etc/systemd/system/lawroad-backend.service
```

```ini
[Unit]
Description=LawNRoad Backend Service
After=network.target

[Service]
User=root
WorkingDirectory=/opt/law-n-road-release
ExecStart=/usr/bin/java -jar /opt/law-n-road-release/build/libs/law-n-road-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:mysql://10.0.2.7:3306/law_n_road \
  --spring.datasource.username=lawnroad \
  --spring.datasource.password=Pass1234!@ \
  --spring.profiles.active=prod
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
