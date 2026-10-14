const User = require('../models/User');

function generateSmartRoadmap(currentSkill, goal, experience) {
    const steps = [
        { title: `${currentSkill} refresher & core fundamentals`, description: `Review foundational concepts of ${currentSkill} and syntax best practices for a ${experience} level.`, completed: false },
        { title: `Development environment & tooling setup`, description: `Configure your workspace, CLI tools, and dependencies required for ${goal}.`, completed: false },
        { title: `Core principles & intermediate patterns`, description: `Master data flow, state management, and architectural patterns relevant to ${goal}.`, completed: false },
        { title: `API integration & asynchronous programming`, description: `Learn how to fetch data from APIs, handle network errors, and manage asynchronous workflows.`, completed: false },
        { title: `Advanced techniques & best practices`, description: `Explore optimization, component structuring, and robust error handling.`, completed: false },
        { title: `Testing, debugging & quality assurance`, description: `Write unit tests and debug common issues to ensure production-ready code.`, completed: false },
        { title: `Capstone Project: Build a real-world ${goal} application`, description: `Apply everything you've learned by planning, building, and finishing a complete capstone project.`, completed: false }
    ];

    return {
        title: `Roadmap: ${currentSkill} ➔ ${goal}`,
        description: `Personalized ${experience}-level learning path tailored to guide you from ${currentSkill} to ${goal}.`,
        steps: steps
    };
}

exports.generateRoadmap = async (req, res) => {
    try {
        const { currentSkill, goal, experience } = req.body;
        if (!currentSkill || !goal) {
            return res.status(400).json({ message: 'Current skill and goal are required' });
        }

        const exp = experience || 'Beginner';

        if (process.env.OPENAI_API_KEY) {
            try {
                const OpenAI = require('openai');
                const openai = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });
                const prompt = `Generate a structured learning roadmap for a ${exp} learner who currently knows "${currentSkill}" and wants to become a "${goal}". Return ONLY a valid JSON object with keys "title", "description", and "steps" (an array of objects with "title", "description", and boolean "completed": false).`;

                const completion = await openai.chat.completions.create({
                    model: "gpt-3.5-turbo",
                    messages: [{ role: "user", content: prompt }],
                    response_format: { type: "json_object" }
                });

                const parsed = JSON.parse(completion.choices[0].message.content);
                return res.json(parsed);
            } catch (aiErr) {
                console.error('AI API error, falling back to smart generator:', aiErr.message);
            }
        }

        const roadmap = generateSmartRoadmap(currentSkill, goal, exp);
        res.json(roadmap);
    } catch (err) {
        console.error('Generate roadmap error:', err);
        res.status(500).json({ message: 'Server error generating roadmap' });
    }
};
