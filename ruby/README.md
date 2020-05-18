# Azure IoT Central ARM Ruby SDK samples

Sample code for using [azure_mgmt_iot_central](https://rubygems.org/gems/azure_mgmt_iot_central/versions/0.18.0) SDK provided by [Microsoft Azure](https://github.com/Azure). This sample code will check if the resource name is available, create or update an app, retrieve app information, update app, list all the apps that belongs to the specific resource group, and finally delete the app if uncommented.

## Get started

### Prerequisites
- [Ruby](https://rubyinstaller.org/downloads/)
- A resource group called **myResourceGroup** in your Azure subscription

### Installation
To begin, simply clone this repository onto your local machine and run the following to install all the necessary dependencies.

```
gem install "azure_mgmt_iot_central"
gem install "ms_rest_azure"
```

### Usage
Make sure you head over to the run.py to change the configuration to the one that is shown on your [Microsoft Azure Portal](https://portal.azure.com).

```
ruby example.rb
```
