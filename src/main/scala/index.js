const express = require('express')
const app = express()
const port = 8080

app.use(express.json())

app.post('/try', (req, res) => {
    console.log(req.body + ' try in console(req)')
    res.send('res')
})

app.post('/posttest', (req, res) => {
    console.log(req.body + ' try in console(req)')
    res.send('res')
})

app.listen(port, () => {
    console.log(`listening on port ${port}`)
})
