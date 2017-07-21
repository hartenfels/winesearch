export function configure(config) {
  config.globalResources([
    './value-converters/humanize',
    './value-converters/hyphenize',
    './value-converters/stars',
  ]);
}
