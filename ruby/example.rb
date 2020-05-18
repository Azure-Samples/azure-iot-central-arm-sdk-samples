require "azure_mgmt_iot_central"
require "ms_rest_azure"

# TODO: Fill this in
provider = MsRestAzure::ApplicationTokenProvider.new(
       'tenant_id',
       'client_id',
       'client_secret')
credentials = MsRest::TokenCredentials.new(provider)

# TODO: Fill this in
options = {
    tenant_id: 'tenant_id',
    client_id: 'client_id',
    client_secret: 'client_secret',
    subscription_id: 'subscription_id',
    credentials: credentials
}

# TODO: Add name and resourceGroup
name = 'some-app-name'
resourceGroup = 'myResourceGroup'

app =  Azure::IotCentral::Mgmt::V2018_09_01::Models::App::new()
app.location = 'unitedstates'
app.display_name = 'Ruby SDK Application'
app.subdomain = name

skuInfo =  Azure::IotCentral::Mgmt::V2018_09_01::Models::AppSkuInfo::new()
skuInfo.name = 'ST2'

app.sku = skuInfo

c = Azure::IotCentral::Profiles::Latest::Mgmt::Client::new(options)
apps =  Azure::IotCentral::Mgmt::V2018_09_01::Apps::new(c)

# check if the name available
operationInputs = Azure::IotCentral::Mgmt::V2018_09_01::Models::OperationInputs::new()
operationInputs.name = name
nameAvailable = apps.check_name_availability(operationInputs)
pp nameAvailable

# create app
newapp = apps.create_or_update(resourceGroup, name, app)
pp newapp

# get app
getApp = apps.get(resourceGroup, name)
pp getApp

appPatch =  Azure::IotCentral::Mgmt::V2018_09_01::Models::AppPatch::new()
appPatch.display_name = name + '-new-name'

# update app
updateApp = apps.update(resourceGroup, name, appPatch)
pp updateApp

# list apps under the resource group
result = apps.list_by_resource_group(resourceGroup)
pp result

# delete app
# deleteResult = apps.delete(resourceGroup, name)
# pp deleteResult
