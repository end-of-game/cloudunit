import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';

import { environment } from './../../environments/environment.prod';

@Injectable()
export class HttpService {

//   private BASE_URL: string = environment.API_BASE_URL;

  private _headers = new Headers({'Content-Type': 'application/json; charset=utf-8'});

  constructor(private http: Http) { }

  // Header 
  public appendHeader(name: string, value: string) {
      this._headers.append(name, value);
  }

  public deleteHeader(name: string) {
      this._headers.delete(name);
  }

  public setHeader(headers: Headers) {
      this._headers = headers;
  }

  // method
  public get(url: string){
    return this.http.get(url, {headers: this._headers});
  }

  public post(url: string, body: any) {
      return this.http.post(url, body, {headers: this._headers});
  }


  public put(url: string, body: any) {
      return this.http.put(url, body, {headers: this._headers});
  }

  public patch(url: string, body: any) {
      return this.http.patch(url, body, {headers: this._headers});
  }
  
  public head(url: string) {
      return this.http.head(url, {headers: this._headers});
  }

  public options(url: string) {
      return this.http.options(url, {headers: this._headers});
  }

  public delete(url: string) {
      return this.http.delete(url, {headers: this._headers});
  }
  
}
