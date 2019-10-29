Overview
---
kotlin + Spring Boot
通常のID/PWに加えて、仮想MFAデバイスであるGoogleAuthenticatorを利用した
２要素認証をSpring Securityを使って構築するサンプルです。

### エンドポイント一覧

#### アカウント作成

**http://localhost:8080/account/create**

**Request Body**
```
{
	"userId" : "takahiro310",
	"email" : "test@takahiro310.com",
	"password" : "password"
}
```
**Resonse Body**
```
{
    "token": "c9b07fb7-e8b4-487e-8da8-712274ead1af"
}
```

#### メール認証

**http://localhost:8080/account/verify-email**

**Request Body**
```
{
	"token": "c9b07fb7-e8b4-487e-8da8-712274ead1af"
}
```
**Resonse Body**
```
{
    "qrCode": "https://chart.googleapis.com/chart?chs=160x160&chld=M%7C0&cht=qr&chl=otpauth%3A%2F%2Ftotp%2Ftakahiro%40localhost%3Fsecret%3DBV6QXSD32MGJ2ONP"
}
```

#### ログイン

**http://localhost:8080/login**

**Request Body**
```
{
	"userId": "takahiro310",
	"password": "password",
	"token": "744167"
}
```

レスポンスヘッダ`Authorization`に認可トークンが返答されます。

#### 認可検証用のエンドポイント

**http://localhost:8080/account/test**

リクエストヘッダに`Authorization`を入力してください。
HTTPステータス 200でレスポンスボディに文字が表示されたらOKです。
