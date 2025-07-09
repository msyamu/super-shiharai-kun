# 開発者ガイド

## 設計方針

### アーキテクチャの依存関係

```
Presentation → Application → Domain
     ↓              ↓
Infrastructure ←────┘
```

**重要なルール**:
- Domain層は他の層に依存しない
- Infrastructure層の実装詳細はinterfaceで隠蔽
- Controllerではバリデーションのみでビジネスロジックは書かない

### 実装時の注意点

- **DTOは必ず`@Serializable`を付与**
- **Repositoryは必ずinterfaceを先に定義**
- **UseCaseは全てsuspend関数**
- **エラーハンドリングはController層で統一**

## API設計

### ページネーション実装の注意

- **OFFSET/LIMIT**は大量データで性能劣化
- 10万件超える場合はカーソルベースを検討
- `totalElements`計算はCOUNTクエリで重い

## バリデーション設計

### 重要な考慮点

- **Controller層でのみバリデーション実行**（UseCase層に書かない）
- **正規表現は性能を考慮**（複雑な式は避ける）
- **エラーメッセージは攻撃者に情報を与えない**
