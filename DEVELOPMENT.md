# 開発者ガイド

## 🏗️ アーキテクチャ概要

Clean Architectureパターンを採用し、ビジネスロジックを中心とした設計。  
決済システムの複雑なルールを保守しやすく設計している。

## 📁 ディレクトリ構成と責務

### 📁 domain/ - ビジネスロジックの核
```
domain/
├── model/          # ドメインエンティティ
│   ├── Invoice.kt      # 請求書の業務ルール（手数料計算等）
│   ├── NewInvoice.kt   # 新規請求書作成時のビジネスルール
│   ├── User.kt         # ユーザーエンティティ
│   ├── NewUser.kt      # 新規ユーザー作成時のルール
│   └── Page.kt         # ページネーション
└── repository/     # データアクセスの抽象化
    ├── InvoiceRepository.kt  # 請求書データ操作の契約
    └── UserRepository.kt     # ユーザーデータ操作の契約
```

### 📁 application/ - ユースケース
```
application/
├── usecase/        # ビジネス操作の組み合わせ
│   ├── InvoiceRegistrationUseCase.kt  # 請求書作成の流れ
│   ├── InvoiceListUseCase.kt          # 請求書一覧取得の流れ
│   ├── LoginUseCase.kt                # ログイン処理の流れ
│   └── UserRegistrationUseCase.kt     # ユーザー登録の流れ
└── error/          # アプリケーション例外
    └── AuthenticationException.kt
```

**何を書くべきか**:
- **usecase/**: 複数のdomainオブジェクトを組み合わせた処理
- **error/**: ビジネス例外の定義

### 📁 infrastructure/ - 外部システム連携
```
infrastructure/
├── api/route/      # HTTPルート定義
│   ├── HealthRoute.kt     # ヘルスチェック
│   ├── InvoiceRoute.kt    # /api/v1/invoices のルート
│   └── UserRoute.kt       # /api/v1/auth のルート
├── config/         # 設定・認証・ログ
│   ├── AppConfig.kt       # 環境変数の取得
│   ├── Authentication.kt  # JWT認証設定
│   ├── ErrorHandling.kt   # グローバルエラーハンドラ
│   ├── RequestValidation.kt # リクエストバリデーション設定
│   └── Constants.kt       # アプリケーション定数
├── database/       # DB関連
│   ├── Database.kt        # DB接続設定
│   ├── InvoiceTable.kt    # 請求書テーブル定義
│   └── UserTable.kt       # ユーザーテーブル定義
├── repository/     # Repository実装
│   ├── InvoiceRepositoryImpl.kt  # 実際のDB操作
│   └── UserRepositoryImpl.kt     # 実際のDB操作
└── service/        # 外部サービス
    └── JwtService.kt      # JWT操作
```

**何を書くべきか**:
- **api/route/**: HTTPリクエストの受け口のみ（ビジネスロジック禁止）
- **config/**: 設定値の取得、認証設定
- **database/**: Exposedテーブル定義
- **repository/**: SQLクエリの実装
- **service/**: JWT、暗号化等の技術的処理

### 📁 presentation/ - 入出力とバリデーション
```
presentation/
├── controller/     # リクエスト処理
│   ├── InvoiceController.kt  # 請求書API制御
│   └── UserController.kt     # ユーザーAPI制御
├── dto/           # データ転送オブジェクト
│   ├── BaseRequest.kt                # 共通リクエスト基底クラス
│   ├── InvoiceRegistrationRequest.kt # 請求書作成リクエスト
│   ├── InvoiceResponse.kt            # 請求書レスポンス
│   ├── LoginRequest.kt               # ログインリクエスト
│   ├── LoginResponse.kt              # ログインレスポンス
│   ├── PaginatedResponse.kt          # ページネーションレスポンス
│   ├── UserRegistrationRequest.kt    # ユーザー登録リクエスト
│   └── UserResponse.kt               # ユーザーレスポンス
└── serializer/     # シリアライザー
    ├── BigDecimalSerializer.kt   # 金額のシリアライゼーション
    └── LocalDateSerializer.kt    # 日付のシリアライゼーション
```

**何を書くべきか**:
- **controller/**: バリデーション実行、UseCase呼び出し、レスポンス生成
- **dto/**: API入出力の形式定義（必ず`@Serializable`）
- **serializer/**: JSON ↔ Kotlinオブジェクト変換（BigDecimal、LocalDate等）

### 📁 test/ - テスト構成
```
test/
├── kotlin/
│   ├── application/usecase/    # ユースケースのテスト
│   ├── domain/model/           # ドメインロジックのテスト
│   ├── infrastructure/
│   │   ├── api/route/          # ルートの統合テスト
│   │   ├── repository/         # リポジトリのテスト
│   │   └── service/            # サービスのテスト
│   ├── presentation/
│   │   ├── controller/         # コントローラーのテスト
│   │   └── dto/                # DTOバリデーションテスト
│   └── util/                   # テストユーティリティ
└── resources/
    └── application-test.conf    # テスト用設定
```

## 🎯 レイヤー間のルール

### 依存関係の方向
```
presentation → application → domain
     ↓              ↓
infrastructure ← ← ← ← ←
```

**絶対に守るべきこと**:
- domainは他のレイヤーに依存しない
- infrastructureからapplication/domainを直接呼ばない
- presentationでDB操作やビジネスロジックを書かない

### 各レイヤーでやってはいけないこと

**domain/**:
- HTTPリクエスト/レスポンスの知識を持たない
- データベースの具体的な操作を知らない
- JWT、暗号化等の技術的詳細を知らない

**application/**:
- HTTPステータスコードを決めない
- SQLクエリを書かない
- リクエスト/レスポンスの形式を知らない

**infrastructure/**:
- ビジネスルールを実装しない
- バリデーション以外の判定をしない

**presentation/**:
- データベースに直接アクセスしない
- ビジネス計算をしない（バリデーションのみ）
