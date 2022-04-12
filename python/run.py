from turtle import up
from azure.mgmt.iotcentral import IotCentralClient
from azure.mgmt.iotcentral.models import App, AppSkuInfo, AppPatch, OperationInputs, SystemAssignedServiceIdentity, SystemAssignedServiceIdentityType, PublicNetworkAccess, NetworkRuleSets, NetworkAction
# from azure.common.credentials import UserPassCredentials, get_azure_cli_credentials
from azure.identity import DefaultAzureCredential

# login with az login
subId = "FILL IN SUB ID"
appName = "iot-central-app-tocreate"
resourceGroup = "myResourceGroup"
credential = DefaultAzureCredential()
client = IotCentralClient(credential, subId)

print("\n\nChecking name availability:")
name_check_operation = OperationInputs(name=appName)
result = client.apps.check_name_availability(name_check_operation)
print(result)

print("\n\nCreating or updating application:")
app = App(location="eastus", sku=AppSkuInfo(name="ST2"))
app.subdomain = appName
app.display_name = appName
app.identity = SystemAssignedServiceIdentity(type=SystemAssignedServiceIdentityType.SYSTEM_ASSIGNED)

create_poll = client.apps.begin_create_or_update(resourceGroup, appName, app)
create_result = create_poll.result()
print(create_result)

print("\n\nGet application:")
get_result = client.apps.get(resourceGroup, appName)
print(get_result)

print("\n\nPatching application with new name:")
updateApp = AppPatch()
updateApp.display_name = appName + "-new-name"
updateApp.public_network_access = PublicNetworkAccess.DISABLED
updateApp.network_rule_sets = NetworkRuleSets(apply_to_devices=True, apply_to_io_t_central=False, default_action=NetworkAction.DENY, ip_rules=[])
update_poll = client.apps.begin_update(resourceGroup, appName, updateApp)
update_result = update_poll.result()

patch_result = client.apps.get(resourceGroup, appName)
print(patch_result)

print("\n\nListing all applications in RG:")
appsInGroup = client.apps.list_by_resource_group(resourceGroup)
for item in appsInGroup:
    print(item.display_name)

print("\n\nListing all operations:")
operations = client.operations.list()
for item in operations:
    print(item.name)

print("\n\nListing all app templates:")
appTemplates = client.apps.list_templates()
for item in appTemplates:
    print(item.name)

# print("Deleting application:")
# delete_poll = client.apps.begin_delete(resourceGroup, appName)
# print(delete_poll.result())

print("\n\ndone")
