import { StyleSheet, View, ScrollView, Text } from "react-native"

const styles = StyleSheet.create({
    logInnerContainer: {
        height: "90%",
        width: "100%",
        backgroundColor: "#2f2f2f",
        borderStyle: "solid",
        borderRadius: 25,
        marginBottom: 10,
        elevation: 10,
    },
    logText: {
        color: "white",
        margin: 20,
        fontSize: 12,
    },
})

const introMessage = `
Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Dolor sit amet consectetur adipiscing. Nisl nunc mi ipsum faucibus vitae aliquet nec ullamcorper. Suscipit adipiscing bibendum est ultricies integer quis auctor. Dolor sit amet consectetur adipiscing elit. Enim nec dui nunc mattis enim ut tellus elementum. Ultrices eros in cursus turpis. Arcu cursus euismod quis viverra nibh cras pulvinar mattis. Tellus orci ac auctor augue mauris augue neque. Est ante in nibh mauris cursus mattis. Venenatis cras sed felis eget velit aliquet sagittis id. Egestas pretium aenean pharetra magna ac. Ornare lectus sit amet est placerat in egestas. Vitae ultricies leo integer malesuada nunc vel risus. Nunc faucibus a pellentesque sit amet porttitor. At in tellus integer feugiat scelerisque varius morbi. Facilisis sed odio morbi quis. Elementum sagittis vitae et leo.

Libero nunc consequat interdum varius sit amet mattis vulputate enim. Iaculis eu non diam phasellus vestibulum lorem sed risus ultricies. Fermentum odio eu feugiat pretium nibh ipsum consequat nisl. Mollis nunc sed id semper risus in hendrerit gravida. Quisque id diam vel quam elementum. Risus feugiat in ante metus. Sit amet luctus venenatis lectus magna fringilla urna. Bibendum neque egestas congue quisque egestas diam in. Porttitor massa id neque aliquam. Eget gravida cum sociis natoque penatibus et magnis dis. Viverra adipiscing at in tellus. Ante in nibh mauris cursus mattis molestie a iaculis. Nibh tortor id aliquet lectus. Vitae semper quis lectus nulla at volutpat diam ut venenatis. Mauris sit amet massa vitae tortor condimentum lacinia quis vel.

Eu sem integer vitae justo eget magna. Fermentum odio eu feugiat pretium nibh. Adipiscing elit duis tristique sollicitudin nibh sit amet commodo nulla. Consequat id porta nibh venenatis cras sed felis eget velit. Interdum posuere lorem ipsum dolor sit amet. Purus gravida quis blandit turpis cursus in hac habitasse platea. Pharetra convallis posuere morbi leo urna molestie at elementum. Aliquam eleifend mi in nulla posuere. Risus in hendrerit gravida rutrum quisque non tellus orci. A pellentesque sit amet porttitor eget. Non tellus orci ac auctor augue mauris augue. Sem et tortor consequat id porta nibh. Fusce id velit ut tortor pretium viverra suspendisse.
`

const MessageLog = () => {
    return (
        <View style={styles.logInnerContainer}>
            <ScrollView>
                <Text style={styles.logText}>{introMessage}</Text>
            </ScrollView>
        </View>
    )
}

export default MessageLog
