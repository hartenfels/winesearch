import {bindable, inject, observable} from 'aurelia-framework';
import {Api}                          from '../api';


@inject(Api)
export class Modal {
  @bindable @observable info;

  constructor(api) {
    this.api = api;
  }

  infoChanged(info, _) {
    this.loading = true;

    this.api.get([info.type, info.name])
      .then(res => {
        this.title  = res.title;
        this.body   = res.extract.replace(/[\r\n]+/g, "\n\n").trim();
        this.source = `https://en.wikipedia.org/?curid=${res.pageid}`;
      })
      .catch(err => {
        console.log(err);
        this.title  = "Error";
        this.body   = err.statusText;
        this.source = null;
      })
      .then(() => {
        this.loading = false;
      });
  }
}
