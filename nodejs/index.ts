import * as msRestNodeAuth from "@azure/ms-rest-nodeauth";
import { IotCentralClient } from "@azure/arm-iotcentral";
import { App, OperationInputs, Operation, AppTemplate } from "@azure/arm-iotcentral/src/models/index";
import { AppPatch } from "@azure/arm-iotcentral/dist-esm/models";
import { DefaultAzureCredential } from "@azure/identity";

// const credential = new DefaultAzureCredential();
const SUBSCRIPTIONID: string = "084f6f77-6103-4c18-ad80-d8a5b0bf4478";
const RESOURCEGROUPNAME: string = "myResourceGroup";
const RESOURCENAME: string = "my-app-name5";

const NAME: OperationInputs = {
    name: RESOURCENAME
}
const NEWAPP: App = {
    subdomain: RESOURCENAME,
    sku: {
        name: 'ST2'
    },
    location: 'eastus2',
    displayName: RESOURCENAME
};

const UPDATEAPP: AppPatch = {
    displayName: RESOURCENAME + "-new-name"
};

async function login(): Promise<DefaultAzureCredential> {
    const creds = await new DefaultAzureCredential();
    return new Promise<DefaultAzureCredential>(resolve => resolve(creds));
}

async function checkIfNameExist(creds): Promise<IotCentralClient> {
    const client = new IotCentralClient(creds, SUBSCRIPTIONID);
    const result = await client.apps.checkNameAvailability(NAME);
    console.log(result);
    return new Promise<IotCentralClient>(resolve => resolve(client));
}

async function createOrUpdateApp(client): Promise<IotCentralClient> {
    const result = await client.apps.beginCreateOrUpdate(RESOURCEGROUPNAME, RESOURCENAME, NEWAPP);
    console.log(result);
    return new Promise<IotCentralClient>(resolve => resolve(client));
}

async function retrieveAppInfo(client): Promise<IotCentralClient> {
    const result = await client.apps.get(RESOURCEGROUPNAME, RESOURCENAME)
    console.log(result);
    return new Promise<IotCentralClient>(resolve => resolve(client));
}

async function updateApp(client): Promise<IotCentralClient> {
    const result = await client.apps.beginUpdate(RESOURCEGROUPNAME, RESOURCENAME, UPDATEAPP);
    console.log(result);
    return new Promise<IotCentralClient>(resolve => resolve(client));
}

async function listAllAppsByResourceGroup(client): Promise<IotCentralClient> {
    const result = await client.apps.listByResourceGroup(RESOURCEGROUPNAME);
    console.log(result);
    return new Promise<IotCentralClient>(resolve => resolve(client));
}

async function retrieveOperations(client): Promise<IotCentralClient> {
    const result: [Operation] = await client.operations.list();
    console.log(result);
    return new Promise<IotCentralClient>(resolve => resolve(client));
}

async function retrieveAppTemplates(client): Promise<IotCentralClient> {
    const result: [AppTemplate] = await client.apps.listTemplates();
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
    .then(retrieveOperations)
    .then(retrieveAppTemplates)
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
