// const bluetoothButton = document.getElementById("bluetoothButton")

// bluetoothButton.addEventListener("click", () => {
//     navigator.bluetooth.requestDevice({
//         acceptAllDevices: true,
//         optionalServices: ['6e400002-b5a3-f393-e0a9-e50e24dcca9e'] // Required to access service later.
//       })
//       .then(device => { /* … */ })
//       .catch(error => { console.error(error); });
// });

const bluetoothStart = document.getElementById("read")
navigator.bluetooth.reqeustDevice({filters:[{services:['INTERVOID_004']}] })
.then(device => {
  console.log(device.name);
  return device.gatt.connect();
})
.then(server => {/**/})
.catch(error=>{console.error(error);})