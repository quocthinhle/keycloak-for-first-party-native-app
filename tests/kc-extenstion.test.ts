import KcAdminClient from '@keycloak/keycloak-admin-client';
import {beforeAll, describe, expect, test} from '@jest/globals';
import * as testcontainers from 'testcontainers';
import axios from 'axios';

const CONTEXT_PATH = '.';
const IMAGE_PATH = './kc-extension/Dockerfile';
const KEYCLOAK_ADMIN = 'admin';
const KEYCLOAK_ADMIN_PASSWORD = 'admin';

async function makeFormUrcolondedRequest({
    url,
    body,
}: {
    url: string,
    body: any,
}): Promise<any> {
    try {
        const response = await axios.post(
            url,
            body, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });

        return response.data;
    } catch (error) {
        return (error as any).response?.data;
    }
}

describe('First-party native app authorize', () => {
    let container: testcontainers.StartedTestContainer;
    let keycloakContainerBuilder: testcontainers.GenericContainerBuilder;
    let keycloakHost: string;
    let keycloakPort: number;
    let keycloakAdminInstance: KcAdminClient;

    beforeAll(async () => {
        keycloakContainerBuilder = new testcontainers.GenericContainerBuilder(CONTEXT_PATH, IMAGE_PATH);
        container = await ((await keycloakContainerBuilder.build())
            .withEnvironment({
                'KEYCLOAK_ADMIN': 'admin',
                'KEYCLOAK_ADMIN_PASSWORD': 'admin',
            })
            .withExposedPorts(8080)
            .withCommand(['start-dev'])
            .start());

        keycloakHost = container.getHost();
        keycloakPort = container.getMappedPort(8080);


        keycloakAdminInstance = new KcAdminClient({
            baseUrl: `http://${keycloakHost}:${keycloakPort}`,
            realmName: 'master',
        });

        await keycloakAdminInstance.auth({
            username: KEYCLOAK_ADMIN,
            password: KEYCLOAK_ADMIN_PASSWORD,
            grantType: 'password',
            clientId: 'admin-cli'
        });

        await keycloakAdminInstance.realms.create({
            realm: 'myrealm',
            enabled: true,
        });

        keycloakAdminInstance.setConfig({realmName: 'myrealm'});


        keycloakAdminInstance.clients.create({
            clientId: 'seenow',
            realm: 'myrealm',
            directAccessGrantsEnabled: true,
            publicClient: true,
        });

        keycloakAdminInstance.users.create({
            realm: 'myrealm',
            username: '0905083331',
            enabled: true,
            credentials: [
                { type: 'password', temporary: false, value: '123123' },
            ],
        });

    }, 1 * 360 * 1000);

    test('Register', async () => {
        let deviceSessionCode: any;

        const body: any = {
            client_id: 'seenow',
            scope: 'openid profile',
            phone_number: '0772283591',
            code_challenge: '1el1nXSU92n7Oe7DX89mU_H9R8fksB4qnd8ge3sgM-4',
            code_challenge_method: 'S256',
            response_type: 'code',
        };

        let response = await makeFormUrcolondedRequest({
            url: `http://${keycloakHost}:${keycloakPort}/realms/myrealm/first-party-native-app/authorize`,
            body: new URLSearchParams(body),
        });

        expect(response).toBeTruthy();
        expect(response.error).toBeTruthy();
        expect(response.error).toEqual('otp_required');
        expect(response.device_session).toBeTruthy();

        deviceSessionCode = response.device_session;

        body.device_session = deviceSessionCode;
        body.otp = '123456';

        response = await makeFormUrcolondedRequest({
            url: `http://${keycloakHost}:${keycloakPort}/realms/myrealm/first-party-native-app/authorize`,
            body: new URLSearchParams(body),
        });

        expect(response).toBeTruthy();
        expect(response.error).toBeTruthy();
        expect(response.error).toEqual('passcode_required');
        expect(response.device_session).toBeTruthy();

        deviceSessionCode = response.device_session;
        body.device_session = deviceSessionCode;
        body.passcode = '123123';
        response = await makeFormUrcolondedRequest({
            url: `http://${keycloakHost}:${keycloakPort}/realms/myrealm/first-party-native-app/authorize`,
            body: new URLSearchParams(body),
        });

        expect(response).toBeTruthy();
        expect(response.authorization_code).toBeTruthy();

        // Exchange token
        const exchangeTokenBody = {
            code: response.authorization_code,
            scope: 'openid profile',
            client_id: 'seenow',
            grant_type: 'authorization_code',
            code_verifier: '6y1UBUBkwF769w7DfBvCQwasq57g4985x7YR8cFVkEzsdS_Q0IXZVXbSjEkqcU1BkNsSkT0sQGTN3-cyoGRG6dQVRinCPR0vo4gC_QsxkfNQ4v2CAU2x84UhEqQHK_Rq',
        };

        response = await makeFormUrcolondedRequest({
            url: `http://${keycloakHost}:${keycloakPort}/realms/myrealm/protocol/openid-connect/token`,
            body: new URLSearchParams(exchangeTokenBody),
        });

        expect(response).toBeTruthy();
        expect(response.access_token).toBeTruthy();
        expect(response.refresh_token).toBeTruthy();
    });

    test('Register but failed OTP more than 3 times', async () => {
        let deviceSessionCode: any;

        const body: any = {
            client_id: 'seenow',
            scope: 'openid profile',
            phone_number: '0772283595',
            code_challenge: '1el1nXSU92n7Oe7DX89mU_H9R8fksB4qnd8ge3sgM-4',
            code_challenge_method: 'S256',
            response_type: 'code',
        };

        let response = await makeFormUrcolondedRequest({
            url: `http://${keycloakHost}:${keycloakPort}/realms/myrealm/first-party-native-app/authorize`,
            body: new URLSearchParams(body),
        });

        expect(response).toBeTruthy();
        expect(response.error).toBeTruthy();
        expect(response.error).toEqual('otp_required');
        expect(response.device_session).toBeTruthy();

        for (let i = 0; i < 3; i += 1) {
            deviceSessionCode = response.device_session;

            body.device_session = deviceSessionCode;
            body.otp = '123454';

            response = await makeFormUrcolondedRequest({
                url: `http://${keycloakHost}:${keycloakPort}/realms/myrealm/first-party-native-app/authorize`,
                body: new URLSearchParams(body),
            });

            // The 3rd time enter a wrong otp must respond different
            if (i == 2) {
                expect(response).toBeTruthy();
                expect(response.device_session).toBeTruthy();
                expect(response.error).toEqual('session_cancelled');
            } else {
                expect(response).toBeTruthy();
                expect(response.device_session).toBeTruthy();
                expect(response.error).toEqual('otp_failed');
            }
        }
    })

    test('Login with valid credentials', async () => {
        let deviceSessionCode: any;

        const body: any = {
            client_id: 'seenow',
            scope: 'openid profile',
            phone_number: '0905083331',
            code_challenge: '1el1nXSU92n7Oe7DX89mU_H9R8fksB4qnd8ge3sgM-4',
            code_challenge_method: 'S256',
            response_type: 'code',
        };

        let response = await makeFormUrcolondedRequest({
            url: `http://${keycloakHost}:${keycloakPort}/realms/myrealm/first-party-native-app/authorize`,
            body: new URLSearchParams(body),
        });

        expect(response).toBeTruthy();
        expect(response.error).toBeTruthy();
        expect(response.error).toEqual('passcode_required');
        expect(response.device_session).toBeTruthy();

        deviceSessionCode = response.device_session;

        body.device_session = deviceSessionCode;
        body.passcode = '123123';

        response = await makeFormUrcolondedRequest({
            url: `http://${keycloakHost}:${keycloakPort}/realms/myrealm/first-party-native-app/authorize`,
            body: new URLSearchParams(body),
        });

        expect(response).toBeTruthy();
        expect(response.authorization_code).toBeTruthy();

        // Exchange token
        const exchangeTokenBody = {
            code: response.authorization_code,
            scope: 'openid profile',
            client_id: 'seenow',
            grant_type: 'authorization_code',
            code_verifier: '6y1UBUBkwF769w7DfBvCQwasq57g4985x7YR8cFVkEzsdS_Q0IXZVXbSjEkqcU1BkNsSkT0sQGTN3-cyoGRG6dQVRinCPR0vo4gC_QsxkfNQ4v2CAU2x84UhEqQHK_Rq',
        };

        response = await makeFormUrcolondedRequest({
            url: `http://${keycloakHost}:${keycloakPort}/realms/myrealm/protocol/openid-connect/token`,
            body: new URLSearchParams(exchangeTokenBody),
        });

        expect(response).toBeTruthy();
        expect(response.access_token).toBeTruthy();
        expect(response.refresh_token).toBeTruthy();
    });

    test('Login with invalid credentials', async () => {
        let deviceSessionCode: any;

        const body: any = {
            client_id: 'seenow',
            scope: 'openid profile',
            phone_number: '0905083331',
            code_challenge: '1el1nXSU92n7Oe7DX89mU_H9R8fksB4qnd8ge3sgM-4',
            code_challenge_method: 'S256',
            response_type: 'code',
        };

        let response = await makeFormUrcolondedRequest({
            url: `http://${keycloakHost}:${keycloakPort}/realms/myrealm/first-party-native-app/authorize`,
            body: new URLSearchParams(body),
        });

        expect(response).toBeTruthy();
        expect(response.error).toBeTruthy();
        expect(response.error).toEqual('passcode_required');
        expect(response.device_session).toBeTruthy();

        deviceSessionCode = response.device_session;

        body.device_session = deviceSessionCode;
        body.passcode = '100100';

        response = await makeFormUrcolondedRequest({
            url: `http://${keycloakHost}:${keycloakPort}/realms/myrealm/first-party-native-app/authorize`,
            body: new URLSearchParams(body),
        });

        expect(response).toBeTruthy();
        expect(response.error).toEqual('invalid_credentials');
        expect(response.device_session).toEqual('');
    });
});