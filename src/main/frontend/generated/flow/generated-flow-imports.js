import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/tooltip/src/vaadin-tooltip.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';
import 'react-router';
import 'react';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '5af77f1a06365f154b6a8021652b32a1cd170da07b0ee0e346ae427444938dc5') {
    pending.push(import('./chunks/chunk-d03fc36af15c22640716f21d248ae8940c74d2aa9e5406f8bd9c6a4ea75701a0.js'));
  }
  if (key === 'df2b38cb32f6d7ae7d692abf95c719880290adcbf6d769384613c822338ff1b3') {
    pending.push(import('./chunks/chunk-47cf30d21020d335c4015acb6ba0d09242e8c05158d66026bfd272af5ab0b861.js'));
  }
  if (key === 'bec2593ce6ff00e139f3785d07f5b4041a383a1176c0d48a0001065d6a525f94') {
    pending.push(import('./chunks/chunk-570b096e7df4e4741625e4c88153b217a14e437ceb14562ef68510cadea343f1.js'));
  }
  if (key === 'f75551a24cad8f070f0f8f53e268edb8dc473c6df602139d27201677e986bf13') {
    pending.push(import('./chunks/chunk-d3974a2be8ad2242fc93a894265ddced17215061ed742c53885f19faee1dcb2a.js'));
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