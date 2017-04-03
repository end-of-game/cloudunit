import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs/Rx'
import { Response, Headers } from '@angular/http';

import { HttpService } from '../core/http.service';
import { Connect } from '../models/connect';
import { environment } from './../../environments/environment';

@Injectable()
export class ConnectService {

  private TOKEN_KEY: string = "jwtToken";
  private BASE_URL: string = environment.API_BASE_URL;

  // Observable string Sources
  private _connectSource: BehaviorSubject<Connect> = new BehaviorSubject<Connect>(new Connect());
  // Observable string streams
  public connect$: Observable<Connect> = this._connectSource.asObservable();

  constructor(private httpService: HttpService) {
    if(this.getJwtToken()) {
      this.httpService.appendHeader('authorization', this.getJwtToken());
      this.getUserInformation();
    }
  }
  
  private getUserInformation(): void {  
    this.httpService.get(this.BASE_URL + '/user')
      .map(res => res.json())
      .subscribe(
        data => {
          this._connectSource.next(new Connect(data.username, '', data.email, data.authorities));
        },
        err => { console.log(err) }
      );
  }
  
  public callNoProtected(): Promise<any> {;
    return this.httpService.get(this.BASE_URL + '/persons')
      .map(res => res.json())
      .toPromise();
     
  }
  
  public callProtected(): Promise<any> {
    return this.httpService.get(this.BASE_URL + '/protected')
      .map(res => res.text() )
      .toPromise();    
  }

  public getJwtToken(): string {
      return localStorage.getItem(this.TOKEN_KEY);
  }

  private setJwtToken(token): void {
      localStorage.setItem(this.TOKEN_KEY, token);
  }

  private removeJwtToken(): void {
      localStorage.removeItem(this.TOKEN_KEY);
  }

  login(username: string, password: string): Promise<any> {
    return new Promise((resolve, reject) => {
          this.httpService.post(this.BASE_URL + '/auth', JSON.stringify({username, password}))
          .map(res => res.json())
          .subscribe(
            data => {
              this.setJwtToken(data.token);
              this.httpService.setHeader(
                new Headers({
                  'Content-Type': 'application/json; charset=utf-8',
                  'authorization': data.token
                })
              );
              this.getUserInformation();
              resolve(true)
            },
            err => { reject(err); console.log(err) }
          );
      });
  }

  logout() {
      this.removeJwtToken();
      this.httpService.setHeader(
        new Headers({
          'Content-Type': 'application/json; charset=utf-8'
        })
      );
  }

}
