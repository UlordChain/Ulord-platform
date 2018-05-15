# Ulord Wallet
[中文](ulord_wallet_zh.md)

Ulord wallet is a tool for saving user capital and signing transactions. The Ulord purse has the following categories:
- Desktop SPV wallet, wallet can be run in all kinds of operating systems, and can be accepted, viewed and transferred. Users can recover their wallet through mnemonic words.
- Mobile SPV wallet, wallet can run in iOS and Android, as well as desktop wallet.
- Full node wallet, which has all the account information of block chain, can view transaction information of all addresses, and needs a large amount of disk space.
- Centeralized wallet, managed wallet is similar to a light wallet. It runs on the server side. It is hosted by the Ulord platform. Users can control the proxy signature transaction by password control.

Features of various wallets:

|Type|Safety|Convenience|Recoverability|Recovery method|Use of advice|
|--------|------|------|------|----------|--------------------|
|Desktop SPV wallet|High|convenient|recoverable|Memorizing words|You can manage a large number of coins and keep your wallet keys and memorizing words properly.|
|Mobile SPV wallet|High|convenient|recoverable|Memorizing words|You can manage a large number of coins and keep your wallet keys and memorizing words properly.|
|Full node wallet|High|middle|recoverable|Filesystem backup and Recovery|You can manage large amounts of money and keep good private keys and mnemonic words.|
|Centeralized wallet|middle|convenient|recoverable|Password|It can manage coins that are often needed to circulate, and can restore passwords by forgetting passwords.|

The Ulord platform layer needs to create a managed wallet for each user to sign the transaction for the proxy user.

## UWallet
Ulord wallet mainly provides transaction view and transaction initiation, personal use, and does not have functions related to content.

## UCWallet ([api document](../Uwallet/uwallet_api.md))
Ulord centralization wallet /Ulord content wallet, requires hosting within the Ulord platform, through the payment of password management, has the content transaction related functions.
