# Super Shiharai-kun

支払いサービスのバックエンドAPI - Kotlin + Ktor + PostgreSQLで構築された請求書管理システム

## 🏗️ アーキテクチャ

Clean Architectureパターンを採用。詳細は [DEVELOPMENT.md](./DEVELOPMENT.md) を参照。

## 🚀 クイックスタート

### 必要な環境
- Kotlin 2.1.10
- Docker

### セットアップ（3分）

1. **環境ファイル作成**
   ```bash
   cp .env.example .env
   # .envファイルを編集（必要に応じて）
   ```

2. **起動**
   ```bash
   docker-compose up -d  # データベース起動
   ./gradlew run         # アプリケーション起動
   ```

3. **動作確認**
   ```bash
   curl http://localhost:8080/health
   ```

## 📚 ドキュメント

- **[開発者ガイド](./DEVELOPMENT.md)** - 設計思想、コーディング規約、開発フロー
- **[Swagger UI](http://localhost:8080/swagger)** - API仕様（開発時のみ）

## 🔧 API概要

**認証**: JWT Bearer Token  
**エンドポイント**:
- `GET /health` - ヘルスチェック
- `POST /api/v1/auth/signup` - ユーザー登録  
- `POST /api/v1/auth/login` - ログイン
- `POST /api/v1/invoices` - 請求書作成
- `GET /api/v1/invoices` - 請求書一覧（ページネーション対応）

## ⚡ よく使うコマンド

```bash
# 開発
./gradlew run              # サーバー起動
./gradlew test             # テスト実行
./gradlew build            # ビルド
``
