import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/tooltip/src/vaadin-tooltip.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === 'bec2593ce6ff00e139f3785d07f5b4041a383a1176c0d48a0001065d6a525f94') {
    pending.push(import('./chunks/chunk-5854f9a82aa6fe1e23f2287853beac6ea30feea3d049b47e421f59aeeb163c1a.js'));
  }
  if (key === '5af77f1a06365f154b6a8021652b32a1cd170da07b0ee0e346ae427444938dc5') {
    pending.push(import('./chunks/chunk-3ce744bc01937b08dc24601b19e69d2c70a262096a3f8d30a1d90895557d9c10.js'));
  }
  if (key === 'f75551a24cad8f070f0f8f53e268edb8dc473c6df602139d27201677e986bf13') {
    pending.push(import('./chunks/chunk-6e4a8597b1ba52a1a130e7098755809fb81d91971fcce28f7519ac0cbb310279.js'));
  }
  return Promise.all(pending);
}
window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}