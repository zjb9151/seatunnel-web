import{d as ee,k as B,ao as Eo,V as xe,n as Oe,h as p,r as Fo,A as Oo,ap as Ro,aq as Ao,ae as I,b as F,q as M,K as z,s as G,m as W,e as Bo,f as Q,l as _o,t as J,x as Mo,g as L,j as Re,Z as No,a as Ko,z as _,ar as Lo,w as ge,v as Wo,F as Ho,Y as Z}from"./index.0a188daf.js";import{W as Do}from"./Scrollbar.d76e86d5.js";import{a as Ie}from"./resolve-slot.808959e0.js";import{c as Go}from"./call.9d9a640f.js";import{c as ze}from"./use-form-item.4758a17b.js";import{c as jo,a as Uo}from"./cssr.de22e947.js";import{N as Vo}from"./Popover.b15db28d.js";import{g as qo}from"./VirtualList.0473bfb9.js";import{N as Yo}from"./Suffix.518fe55b.js";import{r as $e}from"./VResizeObserver.10d3934c.js";function Kt(e,t){let{target:o}=e;for(;o;){if(o.dataset&&o.dataset[t]!==void 0)return!0;o=o.parentElement}return!1}const K="v-hidden",Zo=Uo("[v-hidden]",{display:"none!important"}),Te=ee({name:"Overflow",props:{getCounter:Function,getTail:Function,updateCounter:Function,onUpdateCount:Function,onUpdateOverflow:Function},setup(e,{slots:t}){const o=B(null),r=B(null);function a(i){const{value:n}=o,{getCounter:f,getTail:h}=e;let b;if(f!==void 0?b=f():b=r.value,!n||!b)return;b.hasAttribute(K)&&b.removeAttribute(K);const{children:d}=n;if(i.showAllItemsBeforeCalculate)for(const w of d)w.hasAttribute(K)&&w.removeAttribute(K);const m=n.offsetWidth,C=[],g=t.tail?h==null?void 0:h():null;let P=g?g.offsetWidth:0,k=!1;const y=n.children.length-(t.tail?1:0);for(let w=0;w<y-1;++w){if(w<0)continue;const s=d[w];if(k){s.hasAttribute(K)||s.setAttribute(K,"");continue}else s.hasAttribute(K)&&s.removeAttribute(K);const v=s.offsetWidth;if(P+=v,C[w]=v,P>m){const{updateCounter:S}=e;for(let T=w;T>=0;--T){const E=y-1-T;S!==void 0?S(E):b.textContent=`${E}`;const O=b.offsetWidth;if(P-=C[T],P+O<=m||T===0){k=!0,w=T-1,g&&(w===-1?(g.style.maxWidth=`${m-O}px`,g.style.boxSizing="border-box"):g.style.maxWidth="");const{onUpdateCount:x}=e;x&&x(E);break}}}}const{onUpdateOverflow:$}=e;k?$!==void 0&&$(!0):($!==void 0&&$(!1),b.setAttribute(K,""))}const c=Eo();return Zo.mount({id:"vueuc/overflow",head:!0,anchorMetaName:jo,ssr:c}),xe(()=>a({showAllItemsBeforeCalculate:!1})),{selfRef:o,counterRef:r,sync:a}},render(){const{$slots:e}=this;return Oe(()=>this.sync({showAllItemsBeforeCalculate:!1})),p("div",{class:"v-overflow",ref:"selfRef"},[Fo(e,"default"),e.counter?e.counter():p("span",{style:{display:"inline-block"},ref:"counterRef"}),e.tail?e.tail():null])}});function Jo(e,t){t&&(xe(()=>{const{value:o}=e;o&&$e.registerHandler(o,t)}),Oo(()=>{const{value:o}=e;o&&$e.unregisterHandler(o)}))}const Lt=ee({props:{onFocus:Function,onBlur:Function},setup(e){return()=>p("div",{style:"width: 0; height: 0",tabindex:0,onFocus:e.onFocus,onBlur:e.onBlur})}});function Ee(e){return Array.isArray(e)?e:[e]}const we={STOP:"STOP"};function Ae(e,t){const o=t(e);e.children!==void 0&&o!==we.STOP&&e.children.forEach(r=>Ae(r,t))}function Qo(e,t={}){const{preserveGroup:o=!1}=t,r=[],a=o?i=>{i.isLeaf||(r.push(i.key),c(i.children))}:i=>{i.isLeaf||(i.isGroup||r.push(i.key),c(i.children))};function c(i){i.forEach(a)}return c(e),r}function Xo(e,t){const{isLeaf:o}=e;return o!==void 0?o:!t(e)}function et(e){return e.children}function ot(e){return e.key}function tt(){return!1}function nt(e,t){const{isLeaf:o}=e;return!(o===!1&&!Array.isArray(t(e)))}function rt(e){return e.disabled===!0}function lt(e,t){return e.isLeaf===!1&&!Array.isArray(t(e))}function pe(e){var t;return e==null?[]:Array.isArray(e)?e:(t=e.checkedKeys)!==null&&t!==void 0?t:[]}function Ce(e){var t;return e==null||Array.isArray(e)?[]:(t=e.indeterminateKeys)!==null&&t!==void 0?t:[]}function at(e,t){const o=new Set(e);return t.forEach(r=>{o.has(r)||o.add(r)}),Array.from(o)}function it(e,t){const o=new Set(e);return t.forEach(r=>{o.has(r)&&o.delete(r)}),Array.from(o)}function st(e){return(e==null?void 0:e.type)==="group"}function Wt(e){const t=new Map;return e.forEach((o,r)=>{t.set(o.key,r)}),o=>{var r;return(r=t.get(o))!==null&&r!==void 0?r:null}}class ct extends Error{constructor(){super(),this.message="SubtreeNotLoadedError: checking a subtree whose required nodes are not fully loaded."}}function dt(e,t,o,r){return X(t.concat(e),o,r,!1)}function ut(e,t){const o=new Set;return e.forEach(r=>{const a=t.treeNodeMap.get(r);if(a!==void 0){let c=a.parent;for(;c!==null&&!(c.disabled||o.has(c.key));)o.add(c.key),c=c.parent}}),o}function ht(e,t,o,r){const a=X(t,o,r,!1),c=X(e,o,r,!0),i=ut(e,o),n=[];return a.forEach(f=>{(c.has(f)||i.has(f))&&n.push(f)}),n.forEach(f=>a.delete(f)),a}function me(e,t){const{checkedKeys:o,keysToCheck:r,keysToUncheck:a,indeterminateKeys:c,cascade:i,leafOnly:n,checkStrategy:f,allowNotLoaded:h}=e;if(!i)return r!==void 0?{checkedKeys:at(o,r),indeterminateKeys:Array.from(c)}:a!==void 0?{checkedKeys:it(o,a),indeterminateKeys:Array.from(c)}:{checkedKeys:Array.from(o),indeterminateKeys:Array.from(c)};const{levelTreeNodeMap:b}=t;let d;a!==void 0?d=ht(a,o,t,h):r!==void 0?d=dt(r,o,t,h):d=X(o,t,h,!1);const m=f==="parent",C=f==="child"||n,g=d,P=new Set,k=Math.max.apply(null,Array.from(b.keys()));for(let y=k;y>=0;y-=1){const $=y===0,w=b.get(y);for(const s of w){if(s.isLeaf)continue;const{key:v,shallowLoaded:S}=s;if(C&&S&&s.children.forEach(x=>{!x.disabled&&!x.isLeaf&&x.shallowLoaded&&g.has(x.key)&&g.delete(x.key)}),s.disabled||!S)continue;let T=!0,E=!1,O=!0;for(const x of s.children){const N=x.key;if(!x.disabled){if(O&&(O=!1),g.has(N))E=!0;else if(P.has(N)){E=!0,T=!1;break}else if(T=!1,E)break}}T&&!O?(m&&s.children.forEach(x=>{!x.disabled&&g.has(x.key)&&g.delete(x.key)}),g.add(v)):E&&P.add(v),$&&C&&g.has(v)&&g.delete(v)}}return{checkedKeys:Array.from(g),indeterminateKeys:Array.from(P)}}function X(e,t,o,r){const{treeNodeMap:a,getChildren:c}=t,i=new Set,n=new Set(e);return e.forEach(f=>{const h=a.get(f);h!==void 0&&Ae(h,b=>{if(b.disabled)return we.STOP;const{key:d}=b;if(!i.has(d)&&(i.add(d),n.add(d),lt(b.rawNode,c))){if(r)return we.STOP;if(!o)throw new ct}})}),n}function ft(e,{includeGroup:t=!1,includeSelf:o=!0},r){var a;const c=r.treeNodeMap;let i=e==null?null:(a=c.get(e))!==null&&a!==void 0?a:null;const n={keyPath:[],treeNodePath:[],treeNode:i};if(i!=null&&i.ignored)return n.treeNode=null,n;for(;i;)!i.ignored&&(t||!i.isGroup)&&n.treeNodePath.push(i),i=i.parent;return n.treeNodePath.reverse(),o||n.treeNodePath.pop(),n.keyPath=n.treeNodePath.map(f=>f.key),n}function bt(e){if(e.length===0)return null;const t=e[0];return t.isGroup||t.ignored||t.disabled?t.getNext():t}function vt(e,t){const o=e.siblings,r=o.length,{index:a}=e;return t?o[(a+1)%r]:a===o.length-1?null:o[a+1]}function Fe(e,t,{loop:o=!1,includeDisabled:r=!1}={}){const a=t==="prev"?gt:vt,c={reverse:t==="prev"};let i=!1,n=null;function f(h){if(h!==null){if(h===e){if(!i)i=!0;else if(!e.disabled&&!e.isGroup){n=e;return}}else if((!h.disabled||r)&&!h.ignored&&!h.isGroup){n=h;return}if(h.isGroup){const b=ke(h,c);b!==null?n=b:f(a(h,o))}else{const b=a(h,!1);if(b!==null)f(b);else{const d=pt(h);d!=null&&d.isGroup?f(a(d,o)):o&&f(a(h,!0))}}}}return f(e),n}function gt(e,t){const o=e.siblings,r=o.length,{index:a}=e;return t?o[(a-1+r)%r]:a===0?null:o[a-1]}function pt(e){return e.parent}function ke(e,t={}){const{reverse:o=!1}=t,{children:r}=e;if(r){const{length:a}=r,c=o?a-1:0,i=o?-1:a,n=o?-1:1;for(let f=c;f!==i;f+=n){const h=r[f];if(!h.disabled&&!h.ignored)if(h.isGroup){const b=ke(h,t);if(b!==null)return b}else return h}}return null}const Ct={getChild(){return this.ignored?null:ke(this)},getParent(){const{parent:e}=this;return e!=null&&e.isGroup?e.getParent():e},getNext(e={}){return Fe(this,"next",e)},getPrev(e={}){return Fe(this,"prev",e)}};function mt(e,t){const o=t?new Set(t):void 0,r=[];function a(c){c.forEach(i=>{r.push(i),!(i.isLeaf||!i.children||i.ignored)&&(i.isGroup||o===void 0||o.has(i.key))&&a(i.children)})}return a(e),r}function yt(e,t){const o=e.key;for(;t;){if(t.key===o)return!0;t=t.parent}return!1}function Be(e,t,o,r,a,c=null,i=0){const n=[];return e.forEach((f,h)=>{var b;const d=Object.create(r);if(d.rawNode=f,d.siblings=n,d.level=i,d.index=h,d.isFirstChild=h===0,d.isLastChild=h+1===e.length,d.parent=c,!d.ignored){const m=a(f);Array.isArray(m)&&(d.children=Be(m,t,o,r,a,d,i+1))}n.push(d),t.set(d.key,d),o.has(i)||o.set(i,[]),(b=o.get(i))===null||b===void 0||b.push(d)}),n}function Ht(e,t={}){var o;const r=new Map,a=new Map,{getDisabled:c=rt,getIgnored:i=tt,getIsGroup:n=st,getKey:f=ot}=t,h=(o=t.getChildren)!==null&&o!==void 0?o:et,b=t.ignoreEmptyChildren?s=>{const v=h(s);return Array.isArray(v)?v.length?v:null:v}:h,d=Object.assign({get key(){return f(this.rawNode)},get disabled(){return c(this.rawNode)},get isGroup(){return n(this.rawNode)},get isLeaf(){return Xo(this.rawNode,b)},get shallowLoaded(){return nt(this.rawNode,b)},get ignored(){return i(this.rawNode)},contains(s){return yt(this,s)}},Ct),m=Be(e,r,a,d,b);function C(s){if(s==null)return null;const v=r.get(s);return v&&!v.isGroup&&!v.ignored?v:null}function g(s){if(s==null)return null;const v=r.get(s);return v&&!v.ignored?v:null}function P(s,v){const S=g(s);return S?S.getPrev(v):null}function k(s,v){const S=g(s);return S?S.getNext(v):null}function y(s){const v=g(s);return v?v.getParent():null}function $(s){const v=g(s);return v?v.getChild():null}const w={treeNodes:m,treeNodeMap:r,levelTreeNodeMap:a,maxLevel:Math.max(...a.keys()),getChildren:b,getFlattenedNodes(s){return mt(m,s)},getNode:C,getPrev:P,getNext:k,getParent:y,getChild:$,getFirstAvailableNode(){return bt(m)},getPath(s,v={}){return ft(s,v,w)},getCheckedKeys(s,v={}){const{cascade:S=!0,leafOnly:T=!1,checkStrategy:E="all",allowNotLoaded:O=!1}=v;return me({checkedKeys:pe(s),indeterminateKeys:Ce(s),cascade:S,leafOnly:T,checkStrategy:E,allowNotLoaded:O},w)},check(s,v,S={}){const{cascade:T=!0,leafOnly:E=!1,checkStrategy:O="all",allowNotLoaded:x=!1}=S;return me({checkedKeys:pe(v),indeterminateKeys:Ce(v),keysToCheck:s==null?[]:Ee(s),cascade:T,leafOnly:E,checkStrategy:O,allowNotLoaded:x},w)},uncheck(s,v,S={}){const{cascade:T=!0,leafOnly:E=!1,checkStrategy:O="all",allowNotLoaded:x=!1}=S;return me({checkedKeys:pe(v),indeterminateKeys:Ce(v),keysToUncheck:s==null?[]:Ee(s),cascade:T,leafOnly:E,checkStrategy:O,allowNotLoaded:x},w)},getNonLeafKeys(s={}){return Qo(m,s)}};return w}const wt=e=>{const{textColor2:t,primaryColorHover:o,primaryColorPressed:r,primaryColor:a,infoColor:c,successColor:i,warningColor:n,errorColor:f,baseColor:h,borderColor:b,opacityDisabled:d,tagColor:m,closeIconColor:C,closeIconColorHover:g,closeIconColorPressed:P,borderRadiusSmall:k,fontSizeMini:y,fontSizeTiny:$,fontSizeSmall:w,fontSizeMedium:s,heightMini:v,heightTiny:S,heightSmall:T,heightMedium:E,closeColorHover:O,closeColorPressed:x,buttonColor2Hover:N,buttonColor2Pressed:A,fontWeightStrong:j}=e;return Object.assign(Object.assign({},Ao),{closeBorderRadius:k,heightTiny:v,heightSmall:S,heightMedium:T,heightLarge:E,borderRadius:k,opacityDisabled:d,fontSizeTiny:y,fontSizeSmall:$,fontSizeMedium:w,fontSizeLarge:s,fontWeightStrong:j,textColorCheckable:t,textColorHoverCheckable:t,textColorPressedCheckable:t,textColorChecked:h,colorCheckable:"#0000",colorHoverCheckable:N,colorPressedCheckable:A,colorChecked:a,colorCheckedHover:o,colorCheckedPressed:r,border:`1px solid ${b}`,textColor:t,color:m,colorBordered:"rgb(250, 250, 252)",closeIconColor:C,closeIconColorHover:g,closeIconColorPressed:P,closeColorHover:O,closeColorPressed:x,borderPrimary:`1px solid ${I(a,{alpha:.3})}`,textColorPrimary:a,colorPrimary:I(a,{alpha:.12}),colorBorderedPrimary:I(a,{alpha:.1}),closeIconColorPrimary:a,closeIconColorHoverPrimary:a,closeIconColorPressedPrimary:a,closeColorHoverPrimary:I(a,{alpha:.12}),closeColorPressedPrimary:I(a,{alpha:.18}),borderInfo:`1px solid ${I(c,{alpha:.3})}`,textColorInfo:c,colorInfo:I(c,{alpha:.12}),colorBorderedInfo:I(c,{alpha:.1}),closeIconColorInfo:c,closeIconColorHoverInfo:c,closeIconColorPressedInfo:c,closeColorHoverInfo:I(c,{alpha:.12}),closeColorPressedInfo:I(c,{alpha:.18}),borderSuccess:`1px solid ${I(i,{alpha:.3})}`,textColorSuccess:i,colorSuccess:I(i,{alpha:.12}),colorBorderedSuccess:I(i,{alpha:.1}),closeIconColorSuccess:i,closeIconColorHoverSuccess:i,closeIconColorPressedSuccess:i,closeColorHoverSuccess:I(i,{alpha:.12}),closeColorPressedSuccess:I(i,{alpha:.18}),borderWarning:`1px solid ${I(n,{alpha:.35})}`,textColorWarning:n,colorWarning:I(n,{alpha:.15}),colorBorderedWarning:I(n,{alpha:.12}),closeIconColorWarning:n,closeIconColorHoverWarning:n,closeIconColorPressedWarning:n,closeColorHoverWarning:I(n,{alpha:.12}),closeColorPressedWarning:I(n,{alpha:.18}),borderError:`1px solid ${I(f,{alpha:.23})}`,textColorError:f,colorError:I(f,{alpha:.1}),colorBorderedError:I(f,{alpha:.08}),closeIconColorError:f,closeIconColorHoverError:f,closeIconColorPressedError:f,closeColorHoverError:I(f,{alpha:.12}),closeColorPressedError:I(f,{alpha:.18})})},xt={name:"Tag",common:Ro,self:wt},kt=xt,Pt={color:Object,type:{type:String,default:"default"},round:Boolean,size:{type:String,default:"medium"},closable:Boolean,disabled:{type:Boolean,default:void 0}},St=F("tag",`
 white-space: nowrap;
 position: relative;
 box-sizing: border-box;
 cursor: default;
 display: inline-flex;
 align-items: center;
 flex-wrap: nowrap;
 padding: var(--n-padding);
 border-radius: var(--n-border-radius);
 color: var(--n-text-color);
 background-color: var(--n-color);
 transition: 
 border-color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 opacity .3s var(--n-bezier);
 line-height: 1;
 height: var(--n-height);
 font-size: var(--n-font-size);
`,[M("strong",`
 font-weight: var(--n-font-weight-strong);
 `),z("border",`
 pointer-events: none;
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 border-radius: inherit;
 border: var(--n-border);
 transition: border-color .3s var(--n-bezier);
 `),z("icon",`
 display: flex;
 margin: 0 4px 0 0;
 color: var(--n-text-color);
 transition: color .3s var(--n-bezier);
 font-size: var(--n-avatar-size-override);
 `),z("avatar",`
 display: flex;
 margin: 0 6px 0 0;
 `),z("close",`
 margin: var(--n-close-margin);
 transition:
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 `),M("round",`
 padding: 0 calc(var(--n-height) / 3);
 border-radius: calc(var(--n-height) / 2);
 `,[z("icon",`
 margin: 0 4px 0 calc((var(--n-height) - 8px) / -2);
 `),z("avatar",`
 margin: 0 6px 0 calc((var(--n-height) - 8px) / -2);
 `),M("closable",`
 padding: 0 calc(var(--n-height) / 4) 0 calc(var(--n-height) / 3);
 `)]),M("icon, avatar",[M("round",`
 padding: 0 calc(var(--n-height) / 3) 0 calc(var(--n-height) / 2);
 `)]),M("disabled",`
 cursor: not-allowed !important;
 opacity: var(--n-opacity-disabled);
 `),M("checkable",`
 cursor: pointer;
 box-shadow: none;
 color: var(--n-text-color-checkable);
 background-color: var(--n-color-checkable);
 `,[G("disabled",[W("&:hover","background-color: var(--n-color-hover-checkable);",[G("checked","color: var(--n-text-color-hover-checkable);")]),W("&:active","background-color: var(--n-color-pressed-checkable);",[G("checked","color: var(--n-text-color-pressed-checkable);")])]),M("checked",`
 color: var(--n-text-color-checked);
 background-color: var(--n-color-checked);
 `,[G("disabled",[W("&:hover","background-color: var(--n-color-checked-hover);"),W("&:active","background-color: var(--n-color-checked-pressed);")])])])]),It=Object.assign(Object.assign(Object.assign({},Q.props),Pt),{bordered:{type:Boolean,default:void 0},checked:Boolean,checkable:Boolean,strong:Boolean,triggerClickOnClose:Boolean,onClose:[Array,Function],onMouseenter:Function,onMouseleave:Function,"onUpdate:checked":Function,onUpdateChecked:Function,internalCloseFocusable:{type:Boolean,default:!0},internalCloseIsButtonTag:{type:Boolean,default:!0},onCheckedChange:Function}),zt=Ko("n-tag"),ye=ee({name:"Tag",props:It,setup(e){const t=B(null),{mergedBorderedRef:o,mergedClsPrefixRef:r,inlineThemeDisabled:a,mergedRtlRef:c}=Bo(e),i=Q("Tag","-tag",St,kt,e,r);_o(zt,{roundRef:J(e,"round")});function n(C){if(!e.disabled&&e.checkable){const{checked:g,onCheckedChange:P,onUpdateChecked:k,"onUpdate:checked":y}=e;k&&k(!g),y&&y(!g),P&&P(!g)}}function f(C){if(e.triggerClickOnClose||C.stopPropagation(),!e.disabled){const{onClose:g}=e;g&&Go(g,C)}}const h={setTextContent(C){const{value:g}=t;g&&(g.textContent=C)}},b=Mo("Tag",c,r),d=L(()=>{const{type:C,size:g,color:{color:P,textColor:k}={}}=e,{common:{cubicBezierEaseInOut:y},self:{padding:$,closeMargin:w,closeMarginRtl:s,borderRadius:v,opacityDisabled:S,textColorCheckable:T,textColorHoverCheckable:E,textColorPressedCheckable:O,textColorChecked:x,colorCheckable:N,colorHoverCheckable:A,colorPressedCheckable:j,colorChecked:oe,colorCheckedHover:te,colorCheckedPressed:ne,closeBorderRadius:V,fontWeightStrong:re,[_("colorBordered",C)]:H,[_("closeSize",g)]:U,[_("closeIconSize",g)]:le,[_("fontSize",g)]:ae,[_("height",g)]:q,[_("color",C)]:ie,[_("textColor",C)]:se,[_("border",C)]:ce,[_("closeIconColor",C)]:Y,[_("closeIconColorHover",C)]:de,[_("closeIconColorPressed",C)]:ue,[_("closeColorHover",C)]:he,[_("closeColorPressed",C)]:fe}}=i.value;return{"--n-font-weight-strong":re,"--n-avatar-size-override":`calc(${q} - 8px)`,"--n-bezier":y,"--n-border-radius":v,"--n-border":ce,"--n-close-icon-size":le,"--n-close-color-pressed":fe,"--n-close-color-hover":he,"--n-close-border-radius":V,"--n-close-icon-color":Y,"--n-close-icon-color-hover":de,"--n-close-icon-color-pressed":ue,"--n-close-icon-color-disabled":Y,"--n-close-margin":w,"--n-close-margin-rtl":s,"--n-close-size":U,"--n-color":P||(o.value?H:ie),"--n-color-checkable":N,"--n-color-checked":oe,"--n-color-checked-hover":te,"--n-color-checked-pressed":ne,"--n-color-hover-checkable":A,"--n-color-pressed-checkable":j,"--n-font-size":ae,"--n-height":q,"--n-opacity-disabled":S,"--n-padding":$,"--n-text-color":k||se,"--n-text-color-checkable":T,"--n-text-color-checked":x,"--n-text-color-hover-checkable":E,"--n-text-color-pressed-checkable":O}}),m=a?Re("tag",L(()=>{let C="";const{type:g,size:P,color:{color:k,textColor:y}={}}=e;return C+=g[0],C+=P[0],k&&(C+=`a${ze(k)}`),y&&(C+=`b${ze(y)}`),o.value&&(C+="c"),C}),d,e):void 0;return Object.assign(Object.assign({},h),{rtlEnabled:b,mergedClsPrefix:r,contentRef:t,mergedBordered:o,handleClick:n,handleCloseClick:f,cssVars:a?void 0:d,themeClass:m==null?void 0:m.themeClass,onRender:m==null?void 0:m.onRender})},render(){var e,t;const{mergedClsPrefix:o,rtlEnabled:r,closable:a,color:{borderColor:c}={},round:i,onRender:n,$slots:f}=this;n==null||n();const h=Ie(f.avatar,d=>d&&p("div",{class:`${o}-tag__avatar`},d)),b=Ie(f.icon,d=>d&&p("div",{class:`${o}-tag__icon`},d));return p("div",{class:[`${o}-tag`,this.themeClass,{[`${o}-tag--rtl`]:r,[`${o}-tag--strong`]:this.strong,[`${o}-tag--disabled`]:this.disabled,[`${o}-tag--checkable`]:this.checkable,[`${o}-tag--checked`]:this.checkable&&this.checked,[`${o}-tag--round`]:i,[`${o}-tag--avatar`]:h,[`${o}-tag--icon`]:b,[`${o}-tag--closable`]:a}],style:this.cssVars,onClick:this.handleClick,onMouseenter:this.onMouseenter,onMouseleave:this.onMouseleave},b||h,p("span",{class:`${o}-tag__content`,ref:"contentRef"},(t=(e=this.$slots).default)===null||t===void 0?void 0:t.call(e)),!this.checkable&&a?p(No,{clsPrefix:o,class:`${o}-tag__close`,disabled:this.disabled,onClick:this.handleCloseClick,focusable:this.internalCloseFocusable,round:i,isButtonTag:this.internalCloseIsButtonTag,absolute:!0}):null,!this.checkable&&this.mergedBordered?p("div",{class:`${o}-tag__border`,style:{borderColor:c}}):null)}}),$t=W([F("base-selection",`
 position: relative;
 z-index: auto;
 box-shadow: none;
 width: 100%;
 max-width: 100%;
 display: inline-block;
 vertical-align: bottom;
 border-radius: var(--n-border-radius);
 min-height: var(--n-height);
 line-height: 1.5;
 font-size: var(--n-font-size);
 `,[F("base-loading",`
 color: var(--n-loading-color);
 `),F("base-selection-tags","min-height: var(--n-height);"),z("border, state-border",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 pointer-events: none;
 border: var(--n-border);
 border-radius: inherit;
 transition:
 box-shadow .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 `),z("state-border",`
 z-index: 1;
 border-color: #0000;
 `),F("base-suffix",`
 cursor: pointer;
 position: absolute;
 top: 50%;
 transform: translateY(-50%);
 right: 10px;
 `,[z("arrow",`
 font-size: var(--n-arrow-size);
 color: var(--n-arrow-color);
 transition: color .3s var(--n-bezier);
 `)]),F("base-selection-overlay",`
 display: flex;
 align-items: center;
 white-space: nowrap;
 pointer-events: none;
 position: absolute;
 top: 0;
 right: 0;
 bottom: 0;
 left: 0;
 padding: var(--n-padding-single);
 transition: color .3s var(--n-bezier);
 `,[z("wrapper",`
 flex-basis: 0;
 flex-grow: 1;
 overflow: hidden;
 text-overflow: ellipsis;
 `)]),F("base-selection-placeholder",`
 color: var(--n-placeholder-color);
 `,[z("inner",`
 max-width: 100%;
 overflow: hidden;
 `)]),F("base-selection-tags",`
 cursor: pointer;
 outline: none;
 box-sizing: border-box;
 position: relative;
 z-index: auto;
 display: flex;
 padding: var(--n-padding-multiple);
 flex-wrap: wrap;
 align-items: center;
 width: 100%;
 vertical-align: bottom;
 background-color: var(--n-color);
 border-radius: inherit;
 transition:
 color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 `),F("base-selection-label",`
 height: var(--n-height);
 display: inline-flex;
 width: 100%;
 vertical-align: bottom;
 cursor: pointer;
 outline: none;
 z-index: auto;
 box-sizing: border-box;
 position: relative;
 transition:
 color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 border-radius: inherit;
 background-color: var(--n-color);
 align-items: center;
 `,[F("base-selection-input",`
 font-size: inherit;
 line-height: inherit;
 outline: none;
 cursor: pointer;
 box-sizing: border-box;
 border:none;
 width: 100%;
 padding: var(--n-padding-single);
 background-color: #0000;
 color: var(--n-text-color);
 transition: color .3s var(--n-bezier);
 caret-color: var(--n-caret-color);
 `,[z("content",`
 text-overflow: ellipsis;
 overflow: hidden;
 white-space: nowrap; 
 `)]),z("render-label",`
 color: var(--n-text-color);
 `)]),G("disabled",[W("&:hover",[z("state-border",`
 box-shadow: var(--n-box-shadow-hover);
 border: var(--n-border-hover);
 `)]),M("focus",[z("state-border",`
 box-shadow: var(--n-box-shadow-focus);
 border: var(--n-border-focus);
 `)]),M("active",[z("state-border",`
 box-shadow: var(--n-box-shadow-active);
 border: var(--n-border-active);
 `),F("base-selection-label","background-color: var(--n-color-active);"),F("base-selection-tags","background-color: var(--n-color-active);")])]),M("disabled","cursor: not-allowed;",[z("arrow",`
 color: var(--n-arrow-color-disabled);
 `),F("base-selection-label",`
 cursor: not-allowed;
 background-color: var(--n-color-disabled);
 `,[F("base-selection-input",`
 cursor: not-allowed;
 color: var(--n-text-color-disabled);
 `),z("render-label",`
 color: var(--n-text-color-disabled);
 `)]),F("base-selection-tags",`
 cursor: not-allowed;
 background-color: var(--n-color-disabled);
 `),F("base-selection-placeholder",`
 cursor: not-allowed;
 color: var(--n-placeholder-color-disabled);
 `)]),F("base-selection-input-tag",`
 height: calc(var(--n-height) - 6px);
 line-height: calc(var(--n-height) - 6px);
 outline: none;
 display: none;
 position: relative;
 margin-bottom: 3px;
 max-width: 100%;
 vertical-align: bottom;
 `,[z("input",`
 font-size: inherit;
 font-family: inherit;
 min-width: 1px;
 padding: 0;
 background-color: #0000;
 outline: none;
 border: none;
 max-width: 100%;
 overflow: hidden;
 width: 1em;
 line-height: inherit;
 cursor: pointer;
 color: var(--n-text-color);
 caret-color: var(--n-caret-color);
 `),z("mirror",`
 position: absolute;
 left: 0;
 top: 0;
 white-space: pre;
 visibility: hidden;
 user-select: none;
 -webkit-user-select: none;
 opacity: 0;
 `)]),["warning","error"].map(e=>M(`${e}-status`,[z("state-border",`border: var(--n-border-${e});`),G("disabled",[W("&:hover",[z("state-border",`
 box-shadow: var(--n-box-shadow-hover-${e});
 border: var(--n-border-hover-${e});
 `)]),M("active",[z("state-border",`
 box-shadow: var(--n-box-shadow-active-${e});
 border: var(--n-border-active-${e});
 `),F("base-selection-label",`background-color: var(--n-color-active-${e});`),F("base-selection-tags",`background-color: var(--n-color-active-${e});`)]),M("focus",[z("state-border",`
 box-shadow: var(--n-box-shadow-focus-${e});
 border: var(--n-border-focus-${e});
 `)])])]))]),F("base-selection-popover",`
 margin-bottom: -3px;
 display: flex;
 flex-wrap: wrap;
 margin-right: -8px;
 `),F("base-selection-tag-wrapper",`
 max-width: 100%;
 display: inline-flex;
 padding: 0 7px 3px 0;
 `,[W("&:last-child","padding-right: 0;"),F("tag",`
 font-size: 14px;
 max-width: 100%;
 `,[z("content",`
 line-height: 1.25;
 text-overflow: ellipsis;
 overflow: hidden;
 `)])])]),Dt=ee({name:"InternalSelection",props:Object.assign(Object.assign({},Q.props),{clsPrefix:{type:String,required:!0},bordered:{type:Boolean,default:void 0},active:Boolean,pattern:{type:String,default:""},placeholder:String,selectedOption:{type:Object,default:null},selectedOptions:{type:Array,default:null},labelField:{type:String,default:"label"},valueField:{type:String,default:"value"},multiple:Boolean,filterable:Boolean,clearable:Boolean,disabled:Boolean,size:{type:String,default:"medium"},loading:Boolean,autofocus:Boolean,showArrow:{type:Boolean,default:!0},inputProps:Object,focused:Boolean,renderTag:Function,onKeydown:Function,onClick:Function,onBlur:Function,onFocus:Function,onDeleteOption:Function,maxTagCount:[String,Number],onClear:Function,onPatternInput:Function,onPatternFocus:Function,onPatternBlur:Function,renderLabel:Function,status:String,inlineThemeDisabled:Boolean,ignoreComposition:{type:Boolean,default:!0},onResize:Function}),setup(e){const t=B(null),o=B(null),r=B(null),a=B(null),c=B(null),i=B(null),n=B(null),f=B(null),h=B(null),b=B(null),d=B(!1),m=B(!1),C=B(!1),g=Q("InternalSelection","-internal-selection",$t,Lo,e,J(e,"clsPrefix")),P=L(()=>e.clearable&&!e.disabled&&(C.value||e.active)),k=L(()=>e.selectedOption?e.renderTag?e.renderTag({option:e.selectedOption,handleClose:()=>{}}):e.renderLabel?e.renderLabel(e.selectedOption,!0):Z(e.selectedOption[e.labelField],e.selectedOption,!0):e.placeholder),y=L(()=>{const l=e.selectedOption;if(!!l)return l[e.labelField]}),$=L(()=>e.multiple?!!(Array.isArray(e.selectedOptions)&&e.selectedOptions.length):e.selectedOption!==null);function w(){var l;const{value:u}=t;if(u){const{value:R}=o;R&&(R.style.width=`${u.offsetWidth}px`,e.maxTagCount!=="responsive"&&((l=h.value)===null||l===void 0||l.sync()))}}function s(){const{value:l}=b;l&&(l.style.display="none")}function v(){const{value:l}=b;l&&(l.style.display="inline-block")}ge(J(e,"active"),l=>{l||s()}),ge(J(e,"pattern"),()=>{e.multiple&&Oe(w)});function S(l){const{onFocus:u}=e;u&&u(l)}function T(l){const{onBlur:u}=e;u&&u(l)}function E(l){const{onDeleteOption:u}=e;u&&u(l)}function O(l){const{onClear:u}=e;u&&u(l)}function x(l){const{onPatternInput:u}=e;u&&u(l)}function N(l){var u;(!l.relatedTarget||!(!((u=r.value)===null||u===void 0)&&u.contains(l.relatedTarget)))&&S(l)}function A(l){var u;!((u=r.value)===null||u===void 0)&&u.contains(l.relatedTarget)||T(l)}function j(l){O(l)}function oe(){C.value=!0}function te(){C.value=!1}function ne(l){!e.active||!e.filterable||l.target!==o.value&&l.preventDefault()}function V(l){E(l)}function re(l){if(l.key==="Backspace"&&!H.value&&!e.pattern.length){const{selectedOptions:u}=e;u!=null&&u.length&&V(u[u.length-1])}}const H=B(!1);let U=null;function le(l){const{value:u}=t;if(u){const R=l.target.value;u.textContent=R,w()}e.ignoreComposition&&H.value?U=l:x(l)}function ae(){H.value=!0}function q(){H.value=!1,e.ignoreComposition&&x(U),U=null}function ie(l){var u;m.value=!0,(u=e.onPatternFocus)===null||u===void 0||u.call(e,l)}function se(l){var u;m.value=!1,(u=e.onPatternBlur)===null||u===void 0||u.call(e,l)}function ce(){var l,u;if(e.filterable)m.value=!1,(l=i.value)===null||l===void 0||l.blur(),(u=o.value)===null||u===void 0||u.blur();else if(e.multiple){const{value:R}=a;R==null||R.blur()}else{const{value:R}=c;R==null||R.blur()}}function Y(){var l,u,R;e.filterable?(m.value=!1,(l=i.value)===null||l===void 0||l.focus()):e.multiple?(u=a.value)===null||u===void 0||u.focus():(R=c.value)===null||R===void 0||R.focus()}function de(){const{value:l}=o;l&&(v(),l.focus())}function ue(){const{value:l}=o;l&&l.blur()}function he(l){const{value:u}=n;u&&u.setTextContent(`+${l}`)}function fe(){const{value:l}=f;return l}function _e(){return o.value}let be=null;function ve(){be!==null&&window.clearTimeout(be)}function Me(){e.disabled||e.active||(ve(),be=window.setTimeout(()=>{$.value&&(d.value=!0)},100))}function Ne(){ve()}function Ke(l){l||(ve(),d.value=!1)}ge($,l=>{l||(d.value=!1)}),xe(()=>{Wo(()=>{const l=i.value;!l||(l.tabIndex=e.disabled||m.value?-1:0)})}),Jo(r,e.onResize);const{inlineThemeDisabled:Pe}=e,Se=L(()=>{const{size:l}=e,{common:{cubicBezierEaseInOut:u},self:{borderRadius:R,color:Le,placeholderColor:We,textColor:He,paddingSingle:De,paddingMultiple:Ge,caretColor:je,colorDisabled:Ue,textColorDisabled:Ve,placeholderColorDisabled:qe,colorActive:Ye,boxShadowFocus:Ze,boxShadowActive:Je,boxShadowHover:Qe,border:Xe,borderFocus:eo,borderHover:oo,borderActive:to,arrowColor:no,arrowColorDisabled:ro,loadingColor:lo,colorActiveWarning:ao,boxShadowFocusWarning:io,boxShadowActiveWarning:so,boxShadowHoverWarning:co,borderWarning:uo,borderFocusWarning:ho,borderHoverWarning:fo,borderActiveWarning:bo,colorActiveError:vo,boxShadowFocusError:go,boxShadowActiveError:po,boxShadowHoverError:Co,borderError:mo,borderFocusError:yo,borderHoverError:wo,borderActiveError:xo,clearColor:ko,clearColorHover:Po,clearColorPressed:So,clearSize:Io,arrowSize:zo,[_("height",l)]:$o,[_("fontSize",l)]:To}}=g.value;return{"--n-bezier":u,"--n-border":Xe,"--n-border-active":to,"--n-border-focus":eo,"--n-border-hover":oo,"--n-border-radius":R,"--n-box-shadow-active":Je,"--n-box-shadow-focus":Ze,"--n-box-shadow-hover":Qe,"--n-caret-color":je,"--n-color":Le,"--n-color-active":Ye,"--n-color-disabled":Ue,"--n-font-size":To,"--n-height":$o,"--n-padding-single":De,"--n-padding-multiple":Ge,"--n-placeholder-color":We,"--n-placeholder-color-disabled":qe,"--n-text-color":He,"--n-text-color-disabled":Ve,"--n-arrow-color":no,"--n-arrow-color-disabled":ro,"--n-loading-color":lo,"--n-color-active-warning":ao,"--n-box-shadow-focus-warning":io,"--n-box-shadow-active-warning":so,"--n-box-shadow-hover-warning":co,"--n-border-warning":uo,"--n-border-focus-warning":ho,"--n-border-hover-warning":fo,"--n-border-active-warning":bo,"--n-color-active-error":vo,"--n-box-shadow-focus-error":go,"--n-box-shadow-active-error":po,"--n-box-shadow-hover-error":Co,"--n-border-error":mo,"--n-border-focus-error":yo,"--n-border-hover-error":wo,"--n-border-active-error":xo,"--n-clear-size":Io,"--n-clear-color":ko,"--n-clear-color-hover":Po,"--n-clear-color-pressed":So,"--n-arrow-size":zo}}),D=Pe?Re("internal-selection",L(()=>e.size[0]),Se,e):void 0;return{mergedTheme:g,mergedClearable:P,patternInputFocused:m,filterablePlaceholder:k,label:y,selected:$,showTagsPanel:d,isComposing:H,counterRef:n,counterWrapperRef:f,patternInputMirrorRef:t,patternInputRef:o,selfRef:r,multipleElRef:a,singleElRef:c,patternInputWrapperRef:i,overflowRef:h,inputTagElRef:b,handleMouseDown:ne,handleFocusin:N,handleClear:j,handleMouseEnter:oe,handleMouseLeave:te,handleDeleteOption:V,handlePatternKeyDown:re,handlePatternInputInput:le,handlePatternInputBlur:se,handlePatternInputFocus:ie,handleMouseEnterCounter:Me,handleMouseLeaveCounter:Ne,handleFocusout:A,handleCompositionEnd:q,handleCompositionStart:ae,onPopoverUpdateShow:Ke,focus:Y,focusInput:de,blur:ce,blurInput:ue,updateCounter:he,getCounter:fe,getTail:_e,renderLabel:e.renderLabel,cssVars:Pe?void 0:Se,themeClass:D==null?void 0:D.themeClass,onRender:D==null?void 0:D.onRender}},render(){const{status:e,multiple:t,size:o,disabled:r,filterable:a,maxTagCount:c,bordered:i,clsPrefix:n,onRender:f,renderTag:h,renderLabel:b}=this;f==null||f();const d=c==="responsive",m=typeof c=="number",C=d||m,g=p(Do,null,{default:()=>p(Yo,{clsPrefix:n,loading:this.loading,showArrow:this.showArrow,showClear:this.mergedClearable&&this.selected,onClear:this.handleClear},{default:()=>{var k,y;return(y=(k=this.$slots).arrow)===null||y===void 0?void 0:y.call(k)}})});let P;if(t){const{labelField:k}=this,y=A=>p("div",{class:`${n}-base-selection-tag-wrapper`,key:A.value},h?h({option:A,handleClose:()=>this.handleDeleteOption(A)}):p(ye,{size:o,closable:!A.disabled,disabled:r,onClose:()=>this.handleDeleteOption(A),internalCloseIsButtonTag:!1,internalCloseFocusable:!1},{default:()=>b?b(A,!0):Z(A[k],A,!0)})),$=(m?this.selectedOptions.slice(0,c):this.selectedOptions).map(y),w=a?p("div",{class:`${n}-base-selection-input-tag`,ref:"inputTagElRef",key:"__input-tag__"},p("input",Object.assign({},this.inputProps,{ref:"patternInputRef",tabindex:-1,disabled:r,value:this.pattern,autofocus:this.autofocus,class:`${n}-base-selection-input-tag__input`,onBlur:this.handlePatternInputBlur,onFocus:this.handlePatternInputFocus,onKeydown:this.handlePatternKeyDown,onInput:this.handlePatternInputInput,onCompositionstart:this.handleCompositionStart,onCompositionend:this.handleCompositionEnd})),p("span",{ref:"patternInputMirrorRef",class:`${n}-base-selection-input-tag__mirror`},this.pattern)):null,s=d?()=>p("div",{class:`${n}-base-selection-tag-wrapper`,ref:"counterWrapperRef"},p(ye,{size:o,ref:"counterRef",onMouseenter:this.handleMouseEnterCounter,onMouseleave:this.handleMouseLeaveCounter,disabled:r})):void 0;let v;if(m){const A=this.selectedOptions.length-c;A>0&&(v=p("div",{class:`${n}-base-selection-tag-wrapper`,key:"__counter__"},p(ye,{size:o,ref:"counterRef",onMouseenter:this.handleMouseEnterCounter,disabled:r},{default:()=>`+${A}`})))}const S=d?a?p(Te,{ref:"overflowRef",updateCounter:this.updateCounter,getCounter:this.getCounter,getTail:this.getTail,style:{width:"100%",display:"flex",overflow:"hidden"}},{default:()=>$,counter:s,tail:()=>w}):p(Te,{ref:"overflowRef",updateCounter:this.updateCounter,getCounter:this.getCounter,style:{width:"100%",display:"flex",overflow:"hidden"}},{default:()=>$,counter:s}):m?$.concat(v):$,T=C?()=>p("div",{class:`${n}-base-selection-popover`},d?$:this.selectedOptions.map(y)):void 0,E=C?{show:this.showTagsPanel,trigger:"hover",overlap:!0,placement:"top",width:"trigger",onUpdateShow:this.onPopoverUpdateShow,theme:this.mergedTheme.peers.Popover,themeOverrides:this.mergedTheme.peerOverrides.Popover}:null,x=(this.selected?!1:this.active?!this.pattern&&!this.isComposing:!0)?p("div",{class:`${n}-base-selection-placeholder ${n}-base-selection-overlay`},p("div",{class:`${n}-base-selection-placeholder__inner`},this.placeholder)):null,N=a?p("div",{ref:"patternInputWrapperRef",class:`${n}-base-selection-tags`},S,d?null:w,g):p("div",{ref:"multipleElRef",class:`${n}-base-selection-tags`,tabindex:r?void 0:0},S,g);P=p(Ho,null,C?p(Vo,Object.assign({},E,{scrollable:!0,style:"max-height: calc(var(--v-target-height) * 6.6);"}),{trigger:()=>N,default:T}):N,x)}else if(a){const k=this.pattern||this.isComposing,y=this.active?!k:!this.selected,$=this.active?!1:this.selected;P=p("div",{ref:"patternInputWrapperRef",class:`${n}-base-selection-label`},p("input",Object.assign({},this.inputProps,{ref:"patternInputRef",class:`${n}-base-selection-input`,value:this.active?this.pattern:"",placeholder:"",readonly:r,disabled:r,tabindex:-1,autofocus:this.autofocus,onFocus:this.handlePatternInputFocus,onBlur:this.handlePatternInputBlur,onInput:this.handlePatternInputInput,onCompositionstart:this.handleCompositionStart,onCompositionend:this.handleCompositionEnd})),$?p("div",{class:`${n}-base-selection-label__render-label ${n}-base-selection-overlay`,key:"input"},p("div",{class:`${n}-base-selection-overlay__wrapper`},h?h({option:this.selectedOption,handleClose:()=>{}}):b?b(this.selectedOption,!0):Z(this.label,this.selectedOption,!0))):null,y?p("div",{class:`${n}-base-selection-placeholder ${n}-base-selection-overlay`,key:"placeholder"},p("div",{class:`${n}-base-selection-overlay__wrapper`},this.filterablePlaceholder)):null,g)}else P=p("div",{ref:"singleElRef",class:`${n}-base-selection-label`,tabindex:this.disabled?void 0:0},this.label!==void 0?p("div",{class:`${n}-base-selection-input`,title:qo(this.label),key:"input"},p("div",{class:`${n}-base-selection-input__content`},h?h({option:this.selectedOption,handleClose:()=>{}}):b?b(this.selectedOption,!0):Z(this.label,this.selectedOption,!0))):p("div",{class:`${n}-base-selection-placeholder ${n}-base-selection-overlay`,key:"placeholder"},p("div",{class:`${n}-base-selection-placeholder__inner`},this.placeholder)),g);return p("div",{ref:"selfRef",class:[`${n}-base-selection`,this.themeClass,e&&`${n}-base-selection--${e}-status`,{[`${n}-base-selection--active`]:this.active,[`${n}-base-selection--selected`]:this.selected||this.active&&this.pattern,[`${n}-base-selection--disabled`]:this.disabled,[`${n}-base-selection--multiple`]:this.multiple,[`${n}-base-selection--focus`]:this.focused}],style:this.cssVars,onClick:this.onClick,onMouseenter:this.handleMouseEnter,onMouseleave:this.handleMouseLeave,onKeydown:this.onKeydown,onFocusin:this.handleFocusin,onFocusout:this.handleFocusout,onMousedown:this.handleMouseDown},P,i?p("div",{class:`${n}-base-selection__border`}):null,i?p("div",{class:`${n}-base-selection__state-border`}):null)}});export{Lt as F,Dt as N,ct as S,Wt as a,ye as b,Ht as c,mt as f,Kt as h,Jo as u};
