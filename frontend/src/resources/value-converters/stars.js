export class StarsValueConverter {
  toView(value, number) {
    let round = Math.round(value * 2) / 2;
    let mod   = round >= number ? '' : round >= number - 0.5 ? '-half-o' : '-o';
    return `fa fa-star${mod}`;
  }
}
