import {bindable, containerless, inject} from 'aurelia-framework';
import {Api}                             from '../api';


@containerless()
@inject(Api, Element)
export class Wine {
  @bindable id;

  constructor(api, element) {
    this.api     = api;
    this.element = element;
  }


  onClick() {
    if (this.loading) {
      return;
    }

    if (this.expandDetails || this.expandError) {
      this.expandDetails = false;
      this.expandError   = false;
      return;
    }

    this.loading = true;

    this.api.getDetails(this.id)
      .then(res => {
        this.details       = res;
        this.expandDetails = true;
      })
      .catch(err => {
        console.log(err);
        this.error       = err;
        this.expandError = true;
      })
      .then(() => {
        this.loading = false;
      });
  }


  wantInfo(type, name) {
    let evt = new CustomEvent('wantinfo', {
      bubbles : true,
      detail  : {type : type, name : name},
    });
    this.element.dispatchEvent(evt);
  }

  onRegion() {
    this.wantInfo('regions', this.details.region);
    return false;
  }

  onMaker() {
    this.wantInfo('wineries', this.details.maker);
    return false;
  }
}
