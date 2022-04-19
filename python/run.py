
import os
import random

from azure.identity._credentials.browser import InteractiveBrowserCredential
from azure.mgmt.iotcentral import IotCentralClient
from azure.mgmt.iotcentral.models import App, AppSkuInfo, SystemAssignedServiceIdentityType

region = "westus"
resourceGroup = "myResourceGroup"
skuName = "ST2"


# Get subscription info from environment.

tenantId = os.environ["AZURE_TENANT_ID"]
subscriptionId = os.environ["AZURE_SUBSCRIPTION_ID"]

if (tenantId is None) or (subscriptionId is None):
    raise Exception("Expected environment variables.")


# Make IOTC client.

credential = InteractiveBrowserCredential(tenant_id = tenantId)
iotc = IotCentralClient(credential, subscriptionId)


# Choose app name.

appName = f"pysdk-{random.randint(100000, 999999)}-{random.randint(100000, 999999)}"


# Define the app configuration.

app = App(location = region, sku = AppSkuInfo(name = skuName))
app.subdomain = appName
app.display_name = appName
app.identity = { "type": "SystemAssigned" }


# Create the app.

print(f"\nCreating {appName}. Check browser window for login.")
poller = iotc.apps.begin_create_or_update(resourceGroup, appName, app)
result = poller.result()

if result.state != "created":
    raise Exception("Expected 'created' state.")

print("OK")


# Make sure it's idempotent.

print(f"\nUpdating {appName}.")
poller = iotc.apps.begin_create_or_update(resourceGroup, appName, app)
result = poller.result()

if result.state != "created":
    raise Exception("Expected 'created' state.")

print("OK")


# List all the apps in the resource group.

print(f"\nListing IoT Central apps in '{resourceGroup}'")
appsInGroup = iotc.apps.list_by_resource_group(resourceGroup)

for item in appsInGroup:
    print(item)


# Update the app tags.

print(f"\nUpdating {appName} tags.")
tag = "mytag"
value = "myvalue"
app.tags = { tag: value }
poller = iotc.apps.begin_create_or_update(resourceGroup, appName, app)
result = poller.result()

if result.tags[tag] != value:
    raise Exception("Expected updated tag.")

print("OK")


# Delete the app.

print(f"\nDeleting {appName}")
poller = iotc.apps.begin_delete(resourceGroup, appName)
result = poller.result()

if result:
    print(result)
    raise Exception("Expected 'None'.")

print("OK")


# Verify that we can't create in geography as we could before.

print("\nMake sure we can't use geography.")
app.location = "unitedstates"
appName = "us-" + appName
app.subdomain = app.display_name = appName

try:
    poller = iotc.apps.begin_create_or_update(resourceGroup, appName, app)
    result = poller.result()
    print("It worked but it shouldn't have!")

except:
    print("OK")


# Verify that S1 SKU is no longer allowed.

print(f"\nMake sure we can't use S1 SKU.")
appNameS1 = "s1-" + appName
app = App(location = region, sku = AppSkuInfo(name = "S1"))
app.subdomain = appNameS1
app.display_name = appNameS1

try:
    poller = iotc.apps.begin_create_or_update(resourceGroup, appNameS1, app)
    result = poller.result()
    print("It worked but it shouldn't have!")

except:
    print("OK")
