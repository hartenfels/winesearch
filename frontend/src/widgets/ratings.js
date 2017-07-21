import {bindable, inject, observable} from 'aurelia-framework';
import {Api}                          from '../api';


@inject(Api)
export class Ratings {
  @bindable @observable info;
  @bindable             user;

  constructor(api) {
    this.api = api;
  }


  applyRatings(res) {
    this.ratings = res.filter(r => r.author !== this.user);
    this.own     = res.find  (r => r.author === this.user);
    if (!this.own && this.user) {
      this.own = {rating : 0, review : ""};
    }
    this.input = Object.assign({}, this.own);
  }

  infoChanged(info, _) {
    this.applyRatings(info.wine.details.ratings);
  }

  onSave() {
    if (this.saving) {
      return false;
    }

    let args = {
      author : this.user,
      rating : +this.input.rating,
      review : this.input.review,
    };

    this.saving = true;
    this.api.postRating(this.info.wine.id, args)
      .then(res => {
        this.info.wine.details = res;
        this.applyRatings(res.ratings);
      })
      .catch(err => {
        console.log(err);
        alert(err.statusText);
      })
      .then(() => {
        this.saving = false;
      });

    return false;
  }


  get ratingsDisabled() {
    return this.saving
        || !this.input
        || +this.input.rating === 0
        || +this.input.rating === +this.own.rating
        && (this.input.review || "").trim() === (this.own.review || "").trim();
  }
}
