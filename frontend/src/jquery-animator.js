import {Animator, TemplatingEngine} from 'aurelia-templating';
import $                            from 'jquery';


export class JQueryAnimator extends Animator {

    enter(element) {
        let e = $(element);
        if (e.hasClass('au-animate')) {
            return new Promise((resolve, reject) => {
                e.slideUp(0).slideDown(400, resolve);
            });
        }
    }

    leave(element) {
        let e = $(element);
        if (e.hasClass('au-animate')) {
            return new Promise((resolve, reject) => {
                e.slideUp(400, resolve);
            });
        }
    }
}


export function configure(config, callback) {
    let animator = config.container.get(JQueryAnimator);
    config.container.get(TemplatingEngine).configureAnimator(animator);
    if (typeof callback === 'function') {
      callback(animator);
    }
}
