import { IotCentralClient } from "@azure/arm-iotcentral";
import { App, OperationInputs, AppPatch } from "@azure/arm-iotcentral";
import { DeviceCodeCredential } from "@azure/identity";

const SUBSCRIPTIONID: string = "add-subscription-id-here";
const TENANTID: string = "add-tenant-id-here";
const CLIENTID: string = "add-client-id-here";
const RESOURCEGROUPNAME: string = "myResourceGroup";
const RESOURCENAME: string = "my-app-name";

const NAME: OperationInputs = {
    name: RESOURCENAME,
};
const NEWAPP: App = {
    subdomain: RESOURCENAME,
    sku: {
        name: "ST2",
    },
    location: "eastus2",
    displayName: RESOURCENAME,
};

const UPDATEAPP: AppPatch = {
    displayName: RESOURCENAME + "-new-name",
    publicNetworkAccess: "Enabled",
    networkRuleSets: {
        applyToDevices: true,
        applyToIoTCentral: false,
        defaultAction: "Allow",
        ipRules: [
            {
                filterName: "Localhost",
                ipMask: "127.0.0.1",
            },
        ],
    },
};

// Login and Verify Credentials
async function login(): Promise<DeviceCodeCredential> {
    const creds = await new DeviceCodeCredential({
        tenantId: TENANTID,
        clientId: CLIENTID,
    });
    return new Promise<DeviceCodeCredential>((resolve) => resolve(creds));
}

// Check if resource name exists
async function checkIfNameExist(creds): Promise<IotCentralClient> {
    console.log("\nChecking if name exists");
    const client = new IotCentralClient(creds, SUBSCRIPTIONID);
    const result = await client.apps.checkNameAvailability(NAME);
    console.log(result);
    return new Promise<IotCentralClient>((resolve) => resolve(client));
}

// Create or update an existing IOTC application
async function createOrUpdateApp(client): Promise<IotCentralClient> {
    console.log("\nCreating or updating");
    const result = await client.apps.beginCreateOrUpdateAndWait(
        RESOURCEGROUPNAME,
        RESOURCENAME,
        NEWAPP
    );
    console.log(result);
    return new Promise<IotCentralClient>((resolve) => resolve(client));
}

// Retrieve application meta data
async function retrieveAppInfo(client): Promise<IotCentralClient> {
    console.log("\nRetrieving App Info");
    const result = await client.apps.get(RESOURCEGROUPNAME, RESOURCENAME);
    console.dir(result, {
        depth: null,
    });
    return new Promise<IotCentralClient>((resolve) => resolve(client));
}

// Update meta data in IOTC application
async function updateApp(client): Promise<IotCentralClient> {
    console.log("\nUpdating App");
    const result = await client.apps.beginUpdateAndWait(
        RESOURCEGROUPNAME,
        RESOURCENAME,
        UPDATEAPP
    );
    console.dir(result, {
        depth: null,
    });
    return new Promise<IotCentralClient>((resolve) => resolve(client));
}

// List all apps under the resource group
async function listAllAppsByResourceGroup(client): Promise<IotCentralClient> {
    console.log("\nAll Apps in Resource Group");
    for await (const result of client.apps.listByResourceGroup(
        RESOURCEGROUPNAME
    )) {
        console.log(result);
    }
    return new Promise<IotCentralClient>((resolve) => resolve(client));
}

// List all the operations that are supported by IOTC
async function retrieveOperations(client): Promise<IotCentralClient> {
    console.log("\nRetrieve Operations");
    for await (const result of client.operations.list()) {
        console.log(result.name);
    }
    return new Promise<IotCentralClient>((resolve) => resolve(client));
}

// List all the iotc app templates
async function retrieveAppTemplates(client): Promise<IotCentralClient> {
    console.log("\nApp Templates");
    for await (const result of client.apps.listTemplates()) {
        console.log(result.name);
    }
    return new Promise<IotCentralClient>((resolve) => resolve(client));
}

// List all private endpoint connections
async function retrievePrivateEndpointConnections(
    client
): Promise<IotCentralClient> {
    console.log("\nPrivate Endpoint Connections");
    for await (const result of client.privateEndpointConnections.list(
        RESOURCEGROUPNAME,
        RESOURCENAME
    )) {
        console.log(result.privateEndpoint);
    }
    return new Promise<IotCentralClient>((resolve) => resolve(client));
}

// List all private links
async function retrievePrivateLinks(client): Promise<IotCentralClient> {
    console.log("\nAll Private Links in App");
    for await (const result of client.privateLinks.list(
        RESOURCEGROUPNAME,
        RESOURCENAME
    )) {
        console.log(result.name, result.id);
    }
    return new Promise<IotCentralClient>((resolve) => resolve(client));
}

// // Delete an application
// async function deleteApp(client): Promise<IotCentralClient> {
//     console.log("\nDeleting App")
//     const result = await client.apps.beginDeleteAndWait(RESOURCEGROUPNAME, RESOURCENAME);
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
    .then(retrievePrivateEndpointConnections)
    .then(retrievePrivateLinks)
    // .then(deleteApp)
    .then(() => {
        console.log("done");
    })
    .catch((err) => {
        console.log("An error occurred:");
        console.dir(err, {
            depth: null,
            colors: true,
        });
    });
