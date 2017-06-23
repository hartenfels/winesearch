export class HyphenizeValueConverter {

  toView(value) {
    return value.replace(/(\S)\s+(\S)/, '$1-$2');
  }
}
