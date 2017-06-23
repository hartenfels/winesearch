import {bindable, inject} from 'aurelia-framework';


@inject(Element)
export class Criterion {
  @bindable argument;
  @bindable criteria;

  constructor(element) {
    this.element    = element;
    this.categories = [
      {value : 'body',   title : 'Body'     },
      {value : 'color',  title : 'Color'    },
      {value : 'flavor', title : 'Flavor'   },
      {value : 'maker',  title : 'Winery'   },
      {value : 'region', title : 'Region'   },
      {value : 'sugar',  title : 'Sweetness'},
    ];
  }

  attached() {
    this.selectedCategory = this.categories[0].value;
    this.onSelectCategory();
  }

  onSelectCategory() {
    this.options        = this.criteria[this.selectedCategory];
    this.selectedOption = this.options[0];
    this.updateArgument();
  }

  updateArgument() {
    this.argument.category = this.selectedCategory;
    this.argument.option   = this.selectedOption;
  }

  onRemove() {
    let evt = new CustomEvent('remove', {
      bubbles : true,
      detail  : {criterion : this},
    });
    this.element.dispatchEvent(evt);
  }
}
