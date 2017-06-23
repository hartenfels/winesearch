export class HumanizeValueConverter {

  toView(value) {
    return value.replace(/(\S)([A-Z])/g, '$1 $2')
                .replace(/(D)([A-Z])/g,  "$1'$2")
                .replace(/(\S)(\d+)/g,   '$1 $2')
                .replace(/U SRegion/g,   'US Region');
  }
}
