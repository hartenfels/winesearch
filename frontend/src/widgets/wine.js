import {bindable, containerless, inject} from 'aurelia-framework';
import {Api}                             from '../api';


@containerless()
@inject(Api)
export class Wine {
  @bindable id;

  constructor(api) {
    this.api = api;
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
}
