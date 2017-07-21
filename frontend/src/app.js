import {Api}    from './api';
import {inject} from 'aurelia-framework';
import $        from 'jquery';


@inject(Api)
export class App {
  searchArguments = [];

  constructor(api) {
    this.api = api;
  }


  activate() {
    this.loadCriteria();
    this.searchWines();
  }


  loadCriteria() {
    this.criteria      = null;
    this.criteriaError = null;
    this.criteriaLoad  = true;

    this.api.getCriteria()
      .then(res => {
        this.criteria = this.findAndSortArrays(res);
      })
      .catch(err => {
        this.criteriaError = null;
        console.log(err);
      })
      .then(() => {
        this.criteriaLoad = false;
      });
  }

  findAndSortArrays(thing) {
    if (Array.isArray(thing)) {
      thing.sort();
    }
    else {
      for (let key of Object.keys(thing)) {
        this.findAndSortArrays(thing[key]);
      }
    }
    return thing;
  }


  onAddCriterion() {
    this.searchArguments.push({});
  }

  onRemoveCriterion(evt) {
    let arg = evt.detail.criterion.argument;
    this.searchArguments = this.searchArguments.filter(v => v !== arg);
  }

  onClearCriteria() {
    this.searchArguments = [];
  }


  buildSearchArguments() {
    let args = Object.create(null);

    for (let {category, option} of this.searchArguments) {
      (args[category] = args[category] || []).push(option);
    }

    for (let key of Object.keys(args)) {
      args[key] = args[key].join(',');
    }

    return args;
  }

  searchWines() {
    this.wines      = null;
    this.winesError = null;
    this.winesLoad  = true;

    this.api.getWines(this.buildSearchArguments())
      .then(res => {
        res.sort();
        this.wines = res;
      })
      .catch(err => {
        this.winesError = err;
        console.log(err);
      })
      .then(() => {
        this.winesLoad = false;
      });
  }


  get loginInputBlank() {
    return !this.loginInput || !/\S/.test(this.loginInput);
  }

  onLogin() {
    if (!this.loginInputBlank) {
      this.user       = this.loginInput.trim();
      this.loginInput = "";
    }
  }

  onLogout() {
    this.user = null;
    return false;
  }


  onWantInfo(evt) {
    this.modalInfo = evt.detail;
    $('#info-modal').modal('show');
  }

  onWantRatings(evt) {
    this.ratingsInfo = evt.detail;
    $('#ratings-modal').modal('show');
  }
}
