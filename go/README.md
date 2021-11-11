# Azure IoT Central ARM Go SDK samples

Sample code for using [iotcentral](https://github.com/Azure/azure-sdk-for-go/releases/tag/v37.1.0) SDK provided by [Microsoft Azure](https://github.com/Azure). This sample code will check if the resource name is available, create or update an app, retrieve app information, update app, list all the apps that belongs to the specific resource group, and finally delete the app if uncommented.

## Get started

### Prerequisites
- [Go](https://golang.org/doc/install)
- A resource group called **myResourceGroup** in your Azure subscription
- Update main.go file with subscription id, client id, and tenant id information to allow for authorization.

### Installation
To begin, simply clone this repository onto your local machine and run the following to install all the necessary dependencies.

```
go get -u github.com/dimchansky/utfbom
go get -u github.com/mitchellh/go-homedir
go get -u golang.org/x/crypto/pkcs12
go get -u github.com/Azure/azure-sdk-for-go
go get -u github.com/Azure/azure-sdk-for-go/services/iotcentral/mgmt/2021-06-01/iotcentral
go build
```

### Usage
Make sure you head over to the main.go file to change the configuration to the one that is shown on your [Microsoft Azure Portal](https://portal.azure.com).

```
go run .\main.go
```

Ever wonder what Azure SDK for Go provide in terms of iotcentral? Check [this](https://pkg.go.dev/github.com/Azure/azure-sdk-for-go/services/iotcentral/mgmt/2021-06-01/iotcentral) out.
