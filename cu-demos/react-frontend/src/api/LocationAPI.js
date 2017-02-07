class LocationApi {

    static requestHeaders() {
        return {}
    }

    static getAllLocations() {
        const headers = this.requestHeaders();
        const request = new Request(`${process.env.API_HOST}/weather`, {
            method: 'GET',
            headers: headers
        });
        return fetch(request).then(response => {
            return response.json();
        }).catch(error => {
            return error;
        });
    }
}

export default LocationApi;