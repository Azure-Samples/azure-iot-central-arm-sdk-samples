import * as msRestNodeAuth from "@azure/ms-rest-nodeauth";
import { IotCentralClient } from "@azure/arm-iotcentral";
import { App, OperationInputs } from "@azure/arm-iotcentral/src/models/index";
import { AppPatch } from "@azure/arm-iotcentral/esm/models";

const SUBSCRIPTIONID: string = "FILL IN SUB ID";
const RESOURCEGROUPNAME: string = "myResourceGroup";
const RESOURCENAME: string = "my-app-name";

const NAME: OperationInputs = {
    name: RESOURCENAME
}
const NEWAPP: App = {
    subdomain: RESOURCENAME,
    sku: {
        name: 'ST2'
    },
    location: 'unitedstates',
    displayName: RESOURCENAME
};

const UPDATEAPP: AppPatch = {
    displayName: RESOURCENAME + "-new-name"
};

async function login(): Promise<msRestNodeAuth.DeviceTokenCredentials> {
    const creds = await msRestNodeAuth.interactiveLogin();
    return new Promise<msRestNodeAuth.DeviceTokenCredentials>(resolve => resolve(creds));
}

async function checkIfNameExist(creds): Promise<IotCentralClient> {
    const client = new IotCentralClient(creds, SUBSCRIPTIONID);
    const result = await client.apps.checkNameAvailability(NAME);
    console.log(result);
    return new Promise<IotCentralClient>(resolve => resolve(client));
}

async function createOrUpdateApp(client): Promise<IotCentralClient> {
    const result = await client.apps.createOrUpdate(RESOURCEGROUPNAME, RESOURCENAME, NEWAPP);
    console.log(result);
    return new Promise<IotCentralClient>(resolve => resolve(client));
}

async function retrieveAppInfo(client): Promise<IotCentralClient> {
    const result = await client.apps.get(RESOURCEGROUPNAME, RESOURCENAME)
    console.log(result);
    return new Promise<IotCentralClient>(resolve => resolve(client));
}

async function updateApp(client): Promise<IotCentralClient> {
    const result = await client.apps.update(RESOURCEGROUPNAME, RESOURCENAME, UPDATEAPP);
    console.log(result);
    return new Promise<IotCentralClient>(resolve => resolve(client));
}

async function listAllAppsByResourceGroup(client): Promise<IotCentralClient> {
    const result = await client.apps.listByResourceGroup(RESOURCEGROUPNAME);
    console.log(result);
    return new Promise<IotCentralClient>(resolve => resolve(client));
}

// async function deleteApp(client): Promise<IotCentralClient> {
//     const result = await client.apps.deleteMethod(RESOURCEGROUPNAME, RESOURCENAME);
//     console.log(result);
//     return new Promise<IotCentralClient>(resolve => resolve(client));
// }

login()
    .then(checkIfNameExist)
    .then(createOrUpdateApp)
    .then(retrieveAppInfo)
    .then(updateApp)
    .then(listAllAppsByResourceGroup)
    // .then(deleteApp)
    .then(() => {
        console.log("done");
    })
    .catch(err => {
        console.log('An error occurred:');
        console.dir(err, {
            depth: null,
            colors: true
        });
    });
