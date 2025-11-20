# ===== Build stage =====
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# pom.xml を先にコピーして依存関係だけ先に解決
COPY pom.xml .
RUN mvn -B dependency:go-offline

# プロジェクトの全ファイルをコピー
COPY . .

# jar をビルド
RUN mvn -B clean package -DskipTests


# ===== Run stage =====
FROM eclipse-temurin:17-jre

WORKDIR /app

# builder ステージで作った jar をコピー
COPY --from=builder /app/target/*.jar app.jar

# 起動コマンド
ENTRYPOINT ["java","-jar","/app/app.jar"]

EXPOSE 8080
