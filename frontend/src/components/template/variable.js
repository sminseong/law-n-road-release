import { Node, mergeAttributes } from '@tiptap/core'

export const VariableNode = Node.create({
    name: 'variable',
    group: 'inline',       // inline group
    inline: true,
    atom: true,            // 내부 편집 차단
    selectable: true,      // 클릭 시 전체 선택

    addAttributes() {
        return {
            name: { default: '' },
            description: { default: '' },
        }
    },

    parseHTML() {
        return [{ tag: 'span[data-variable]' }]
    },

    renderHTML({ node, HTMLAttributes }) {
        return [
            'span',
            mergeAttributes(
                { 'data-variable': '', 'data-name': node.attrs.name },
                HTMLAttributes,
            ),
            `#{${node.attrs.name}}`,
        ]
    },

    addInputRules() {
        const type = this.name
        return [
            {
                find: /#\{([\uAC00-\uD7A3\w\s]{1,30})\}$/,
                handler({ state, range, match, chain }) {
                    const name = match[1]
                    if (!name) return
                    chain()
                        .deleteRange(range)
                        .insertContent({ type, attrs: { name, description: name } })
                        .run()
                }
            }
        ]
    },

    addCommands() {
        return {
            setVariable:
                attrs =>
                    ({ commands }) => {
                        return commands.insertContent({ type: this.name, attrs })
                    },
        }
    },
})