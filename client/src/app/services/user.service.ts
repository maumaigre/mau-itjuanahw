import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import User from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
    constructor(private httpClient: HttpClient) { }

    getUsers(){
        return this.httpClient.get(`users`);
    }

    getUserbyID(userID: number){
        return this.httpClient.get(`users/${userID}`);
    }

    addUser(data: User){
        return this.httpClient.post(`users`, data);
    }

    patchUser(userID: number, data: User){
        return this.httpClient.patch(`users/${userID}`, data);
    }

    deleteUser(userID: number){
        return this.httpClient.delete(`users/${userID}`);
    }

}