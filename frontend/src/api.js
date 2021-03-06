import {HttpClient} from 'aurelia-http-client';
import {inject}     from 'aurelia-framework';
import environment  from './environment';
import $            from 'jquery';


@inject(HttpClient)
export class Api {

  constructor(httpClient) {
    (this.httpClient = httpClient).configure(config => {
      let host = environment.testing
               ? 'http://localhost:3000/'
               : location.href;
      config
        .withBaseUrl(host)
        .withInterceptor({
          response     (msg) { return msg.content; },
          requestError (err) { throw this.explain(err); },
          responseError(err) { throw this.explain(err); },
          explain      (err) {
            if (!err.statusCode && !err.statusText) {
              err.statusText = 'Connection Failed';
            }
            else if (!err.statusText) {
              err.statusText = `${err.statusCode}`;
            }
            return err;
          },
        });
    });
  }


  url(pieces, args) {
    let url   = pieces.map(p => encodeURIComponent(p)).join('/');
    let query = Object.assign({nocache : new Date().getTime()}, args || {});
    return url + '?' + $.param(query);
  }

  get(pieces, args = null) {
    return this.httpClient.get(this.url(pieces, args));
  }

  post(pieces, args) {
    return this.httpClient.post(this.url(pieces), JSON.stringify(args));
  }


  getCriteria() {
    return this.get(['criteria']);
  }


  getWines(args) {
    return this.get(['wines'], args);
  }

  getDetails(wineId) {
    return this.get(['wines', wineId]);
  }

  postRating(wineId, args) {
    return this.post(['wines', wineId], args);
  }


  getRegion(regionId) {
    return this.get(['regions', regionId]);
  }
}
