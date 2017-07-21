import {bindable, containerless, inject, observable} from 'aurelia-framework';
import {Api}                                         from '../api';


@containerless()
@inject(Api, Element)
export class Wine {
  @bindable   id;
  @observable details;

  constructor(api, element) {
    this.api     = api;
    this.element = element;
  }


  detailsChanged(res, _) {
    let rs  = res.ratings;
    let len = rs.length;

    res.averageRating   = rs.map(r => r.rating).reduce((a, b) => a + b, 0) / len;
    res.numberOfRatings = len;

    return res;
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


  onRatings() {
    let evt = new CustomEvent('wantratings', {
      bubbles : true,
      detail  : {wine : this},
    });
    this.element.dispatchEvent(evt);
    return false;
  }
}
