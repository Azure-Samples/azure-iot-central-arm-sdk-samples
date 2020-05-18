import os, uuid, sys
from azure.mgmt.iotcentral import IotCentralClient
from azure.mgmt.iotcentral.models import App, AppSkuInfo, AppPatch
from msrestazure.azure_active_directory import MSIAuthentication
from azure.common.credentials import UserPassCredentials, get_azure_cli_credentials

# login with az login
creds = get_azure_cli_credentials()
subId = "FILL IN SUB ID"
appName = "iot-central-app-tocreate"
resourceGroup = "myResourceGroup"

print(creds[0])
print(creds[1])

client = IotCentralClient(creds[0], subId)

result = client.apps.check_name_availability(appName)
print(result)

app = App(location="unitedstates", sku=AppSkuInfo(name="ST2"))
app.subdomain = appName

app.display_name = appName

createResult = client.apps.create_or_update(resourceGroup, appName, app)
print(createResult)

getResult = client.apps.get(resourceGroup, appName)
print(getResult)

updateApp = AppPatch()
updateApp.display_name = appName + "-new-name"

updateResult = client.apps.update(resourceGroup, appName, updateApp)
print(updateResult)

appsInGroup = client.apps.list_by_resource_group(resourceGroup)

appsInGroup.next()
for item in appsInGroup.current_page:
    print(item)

# deleteResult = client.apps.delete(resourceGroup, appName)
# print(deleteResult)

print("done")
